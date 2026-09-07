const logEl = document.getElementById('log');
const accountsEl = document.getElementById('accounts');
const summaryEl = document.getElementById('summary');
const validationEl = document.getElementById('validation');
const concurrentResultEl = document.getElementById('concurrentResult');
const singleResultEl = document.getElementById('singleResult');

let lastSnapshot = null;

function escapeHtml(str) {
  const div = document.createElement('div');
  div.textContent = str;
  return div.innerHTML;
}

function log(msg) {
  const time = new Date().toLocaleTimeString('zh-CN', { hour12: false });
  const cls = msg.startsWith('✗') ? 'log-error' : msg.startsWith('✓') ? 'log-ok' : '';
  logEl.innerHTML = `<div class="${cls}">[${time}] ${escapeHtml(msg)}</div>${logEl.innerHTML}`;
}

async function refresh() {
  try {
    const data = await (await fetch('/api/status')).json();
    accountsEl.innerHTML = data.accounts.map(a =>
      `<div class="card">
        <div>${a.name} (#${a.id})</div>
        <div class="balance">${Number(a.balance).toFixed(2)}</div>
      </div>`
    ).join('');

    summaryEl.innerHTML = `
      <div>Transfers: <b>${data.transferCount}</b></div>
      <div>Ledger entries: <b>${data.ledgerCount}</b></div>
      <div>Expected Ledger: <b>${data.transferCount * 2}</b></div>
    `;

    // Validation
    const ledgerOk = data.ledgerCount === data.transferCount * 2;
    const totalBalance = data.accounts.reduce((sum, a) => sum + Number(a.balance), 0);
    const balanceOk = Math.abs(totalBalance - 2000) < 0.01;

    validationEl.innerHTML = `
      <div class="check ${ledgerOk ? 'ok' : 'error'}">
        ${ledgerOk ? '✓' : '✗'} Ledger integrity: ${ledgerOk ? 'OK' : `FAIL (${data.ledgerCount} ≠ ${data.transferCount * 2})`}
      </div>
      <div class="check ${balanceOk ? 'ok' : 'error'}">
        ${balanceOk ? '✓' : '✗'} Total balance: ${totalBalance.toFixed(2)} ${balanceOk ? '' : '(expected 2000.00)'}
      </div>
    `;

    // Refresh notifications
    await refreshNotifications();

    return data;
  } catch (e) {
    log(`Refresh failed: ${e.message}`);
  }
}

async function refreshNotifications() {
  try {
    const stats = await (await fetch('/api/notifications/stats')).json();
    const notificationsEl = document.getElementById('notifications');
    const total = stats.pending + stats.sent + stats.failed;
    const warningThreshold = 3;  // 降低阈值，更容易触发警告

    notificationsEl.innerHTML = `
      <div class="notification-stats">
        <div class="stat-item">
          <span>Pending:</span> <b class="${stats.pending > warningThreshold ? 'warn' : ''}">${stats.pending}</b>
        </div>
        <div class="stat-item">
          <span>Sent:</span> <b class="ok">${stats.sent}</b>
        </div>
        <div class="stat-item">
          <span>Failed:</span> <b class="${stats.failed > 0 ? 'error' : ''}">${stats.failed}</b>
        </div>
        <div class="stat-item">
          <span>Total:</span> <b>${total}</b>
        </div>
      </div>
      ${stats.pending > warningThreshold ? '<div class="notification-warning">⚠️ Thread pool saturated - notifications queuing up (only 1 worker thread)</div>' : ''}
    `;
  } catch (e) {
    console.error('Failed to refresh notifications:', e);
  }
}

async function post(payload) {
  const res = await fetch('/api/transfers', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });
  if (!res.ok) {
    const raw = await res.text();
    let message = raw;
    try {
      message = JSON.parse(raw).message || raw;
    } catch (e) {
      // response wasn't JSON, fall back to raw text
    }
    throw new Error(message);
  }
  return res.json();
}

async function singleTransfer() {
  try {
    const reqIdInput = document.getElementById('requestId');
    const payload = {
      requestId: reqIdInput.value,
      fromAccountId: +document.getElementById('from').value,
      toAccountId: +document.getElementById('to').value,
      amount: +document.getElementById('amount').value
    };
    const result = await post(payload);
    log(`✓ Transfer #${result.id} completed`);
    singleResultEl.innerHTML = `<div class="check ok">✓ Transfer #${result.id} completed</div>`;

    // Auto-increment requestId for next transfer
    const match = reqIdInput.value.match(/^REQ-(\d+)$/);
    if (match) {
      const nextNum = parseInt(match[1]) + 1;
      reqIdInput.value = `REQ-${String(nextNum).padStart(3, '0')}`;
    }
  } catch (e) {
    log(`✗ Failed: ${e.message}`);
    singleResultEl.innerHTML = `<div class="check error">✗ Failed: ${escapeHtml(e.message)}</div>`;
  }
  refresh();
}

async function runConcurrent() {
  const count = +document.getElementById('concurrency').value || 10;
  const from = +document.getElementById('from').value;
  const to = +document.getElementById('to').value;
  const amount = +document.getElementById('amount').value;

  // Take snapshot before test
  const before = await refresh();
  lastSnapshot = {
    balances: before.accounts.reduce((m, a) => ({...m, [a.id]: Number(a.balance)}), {}),
    transferCount: before.transferCount
  };

  log(`Starting ${count} concurrent transfers...`);

  // Build transfer list UI
  const transferItems = [];
  for (let i = 0; i < count; i++) {
    transferItems.push(`<div class="transfer-item pending" id="t${i}">
      <span class="transfer-num">#${i + 1}</span>
      <span class="transfer-status">⋯ Pending</span>
    </div>`);
  }
  concurrentResultEl.innerHTML = `<div class="transfer-list">${transferItems.join('')}</div>`;

  // Execute transfers concurrently but update UI individually
  const startTime = Date.now();
  const jobs = [];
  for (let i = 0; i < count; i++) {
    jobs.push(
      post({
        requestId: `BATCH-${Date.now()}-${i}`,
        fromAccountId: from,
        toAccountId: to,
        amount: amount
      })
      .then(result => {
        document.getElementById(`t${i}`).className = 'transfer-item ok';
        document.getElementById(`t${i}`).querySelector('.transfer-status').textContent = `✓ Transfer #${result.id}`;
        return { success: true };
      })
      .catch(err => {
        const msg = err.message || 'Unknown error';
        const shortMsg = msg.length > 60 ? msg.substring(0, 60) + '...' : msg;
        document.getElementById(`t${i}`).className = 'transfer-item error';
        document.getElementById(`t${i}`).querySelector('.transfer-status').innerHTML = `✗ <span title="${msg}">${shortMsg}</span>`;
        return { success: false };
      })
    );
  }

  const results = await Promise.all(jobs);
  const elapsed = Date.now() - startTime;
  const succeeded = results.filter(r => r.success).length;
  const failed = results.filter(r => !r.success).length;

  // Wait for Kafka processing
  await new Promise(r => setTimeout(r, 2000));
  const after = await refresh();

  // Calculate expected vs actual
  const fromBalanceBefore = lastSnapshot.balances[from];
  const fromBalanceAfter = after.accounts.find(a => a.id === from).balance;
  const actualDeducted = fromBalanceBefore - fromBalanceAfter;
  const expectedDeducted = succeeded * amount;
  const lostUpdate = Math.abs(expectedDeducted - actualDeducted);

  // Add summary after the list
  concurrentResultEl.innerHTML += `
    <div class="concurrent-summary">
      <div class="summary-stats">
        <div><span>Succeeded:</span> <b class="ok">${succeeded}</b></div>
        <div><span>Failed:</span> <b class="${failed > 0 ? 'warn' : ''}">${failed}</b></div>
        <div><span>Duration:</span> <b>${elapsed}ms</b></div>
      </div>
      <div class="balance-check">
        <div class="balance-row">
          <span>Expected deduction:</span>
          <span class="mono">${expectedDeducted.toFixed(2)}</span>
        </div>
        <div class="balance-row">
          <span>Actual deduction:</span>
          <span class="mono">${actualDeducted.toFixed(2)}</span>
        </div>
        <div class="balance-row ${lostUpdate > 0.01 ? 'error' : 'ok'}">
          <span>${lostUpdate > 0.01 ? '✗ Lost Update:' : '✓ Consistent:'}</span>
          <span class="mono">${lostUpdate.toFixed(2)}</span>
        </div>
      </div>
    </div>
  `;

  log(`Test complete: ${succeeded} succeeded, ${failed} failed${lostUpdate > 0.01 ? ', LOST UPDATE: ' + lostUpdate.toFixed(2) : ''}`);
}

async function reset() {
  if (!confirm('Reset all data?')) return;
  try {
    await fetch('/api/reset', { method: 'POST' });
    log('✓ Data reset to initial state');
    concurrentResultEl.innerHTML = '';
    lastSnapshot = null;
    refresh();
  } catch (e) {
    log(`✗ Reset failed: ${e.message}`);
  }
}

refresh();
setInterval(refresh, 3000);
