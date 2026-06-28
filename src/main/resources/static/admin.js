/* ============================
   GreenCut — Admin Panel JS
   (Thymeleaf / Spring Boot build)
   Bookings + Services data is rendered server-side.
   This file only handles client-side UI behavior: panel
   switching, search filtering, chart drawing from data the
   server already computed, and the add/edit service modal.
   ============================ */

/* =====================
   PANEL NAVIGATION (dashboard / bookings / sales — all on /admin)
   ===================== */
function showPanel(id) {
  document.querySelectorAll('.admin-panel').forEach(p => p.style.display = 'none');
  const panel = document.getElementById('panel-' + id);
  if (panel) panel.style.display = 'block';

  document.querySelectorAll('.admin-nav a[data-panel]').forEach(a => a.classList.remove('active'));
  const link = document.querySelector(`.admin-nav a[data-panel="${id}"]`);
  if (link) link.classList.add('active');

  const titles = { dashboard: 'Dashboard', bookings: 'Bookings', sales: 'Sales Analytics' };
  const titleEl = document.getElementById('panel-title');
  if (titleEl) titleEl.textContent = titles[id] || 'Admin';

  if (id === 'sales' && !window._salesChartsRendered) {
    renderSalesCharts();
    window._salesChartsRendered = true;
  }
}

/* =====================
   BOOKINGS SEARCH (client-side filter over server-rendered rows)
   ===================== */
function filterBookings(query) {
  const tbody = document.getElementById('bookings-body');
  if (!tbody) return;
  const q = query.trim().toLowerCase();
  let visibleCount = 0;
  tbody.querySelectorAll('tr[data-row]').forEach(row => {
    const haystack = row.dataset.search || '';
    const match = !q || haystack.includes(q);
    row.style.display = match ? '' : 'none';
    if (match) visibleCount++;
  });
  const emptyRow = tbody.querySelector('tr[data-empty-state]');
  if (emptyRow) emptyRow.style.display = visibleCount ? 'none' : '';
}

/* =====================
   SALES CHARTS — drawn from data the server already computed
   (see dashboard.html: monthlyChartData / serviceBreakdownData)
   ===================== */
function renderSalesCharts() {
  const months = (window.monthlyChartData || []);
  const breakdown = (window.serviceBreakdownData || {});

  // Revenue bar chart
  const barChart = document.getElementById('bar-chart');
  if (barChart) {
    const maxRev = Math.max(...months.map(m => m.revenue), 1);
    barChart.innerHTML = '';
    months.forEach(m => {
      const pct = maxRev > 0 ? (m.revenue / maxRev) * 120 : 4;
      barChart.innerHTML += `
        <div class="bar-wrap">
          <div class="bar" style="height:${Math.max(pct, 4)}px">
            <span class="tooltip">$${Math.round(m.revenue).toLocaleString()}</span>
          </div>
          <div class="bar-label">${m.label}</div>
        </div>`;
    });
  }

  // Booking volume bar chart
  const volChart = document.getElementById('vol-chart');
  if (volChart) {
    const maxCnt = Math.max(...months.map(m => m.count), 1);
    volChart.innerHTML = '';
    months.forEach(m => {
      const pct = (m.count / maxCnt) * 120;
      volChart.innerHTML += `
        <div class="bar-wrap">
          <div class="bar" style="height:${Math.max(pct, 4)}px;background:var(--green-dark)">
            <span class="tooltip">${m.count} booking${m.count === 1 ? '' : 's'}</span>
          </div>
          <div class="bar-label">${m.label}</div>
        </div>`;
    });
  }

  // Donut chart — bookings by service
  const donutSvg = document.getElementById('donut-svg');
  const donutLegend = document.getElementById('donut-legend');
  if (donutSvg && donutLegend) {
    const entries = Object.entries(breakdown).slice(0, 6);
    const total = entries.reduce((a, [, cnt]) => a + cnt, 0) || 1;
    const colors = ['#2e7d32', '#4caf50', '#ff6f00', '#1976d2', '#7b1fa2', '#c62828'];
    const r = 52, cx = 64, cy = 64, circ = 2 * Math.PI * r;
    let offset = 0;
    let paths = '';
    entries.forEach(([name, cnt], i) => {
      const pct = cnt / total;
      const dash = pct * circ;
      const gap = circ - dash;
      paths += `<circle cx="${cx}" cy="${cy}" r="${r}" fill="none" stroke="${colors[i % colors.length]}" stroke-width="22"
        stroke-dasharray="${dash} ${gap}" stroke-dashoffset="${-offset * circ}"
        style="transition:stroke-dasharray .6s ease"/>`;
      offset += pct;
    });
    if (!entries.length) {
      paths = `<circle cx="${cx}" cy="${cy}" r="${r}" fill="none" stroke="#e0e0e0" stroke-width="22"/>`;
    }

    donutSvg.innerHTML = `
      <svg width="128" height="128" viewBox="0 0 128 128" class="donut-svg">
        ${paths}
        <text x="64" y="68" text-anchor="middle" font-size="13" fill="#1a5c2a" font-weight="700">${total} total</text>
      </svg>`;

    donutLegend.innerHTML = entries.map(([name, cnt], i) =>
      `<div class="legend-item">
        <div class="legend-dot" style="background:${colors[i % colors.length]}"></div>
        <span>${name.length > 24 ? name.slice(0, 22) + '…' : name} (${cnt})</span>
      </div>`
    ).join('') || '<div style="color:var(--text-light);font-size:.85rem">No bookings yet</div>';
  }
}

/* =====================
   SERVICE / PLAN MODAL  (admin/services.html)
   The modal is a REAL <form> that POSTs to the existing backend
   endpoints — its action + a couple of hidden fields are switched
   between "add" and "edit" mode right before opening.
   ===================== */
function openAddItem(type) {
  const form = document.getElementById('item-form');
  if (!form) return;
  form.action = '/admin/services/add';
  document.getElementById('item-modal-title').textContent =
    type === 'plan' ? 'Add New Plan' : (type === 'addon' ? 'Add New Add-On' : 'Add New Service');
  document.getElementById('item-id-display').textContent = '';
  form.reset();
  document.getElementById('item-type-hidden').value = type;
  document.getElementById('item-modal').classList.add('open');
}

function openEditItem(btn) {
  const form = document.getElementById('item-form');
  if (!form) return;
  const id = btn.dataset.id;
  form.action = `/admin/services/${id}/edit`;
  document.getElementById('item-modal-title').textContent = 'Edit Item';
  document.getElementById('item-id-display').textContent = '#' + id;

  document.getElementById('item-icon').value = btn.dataset.icon || '';
  document.getElementById('item-name').value = btn.dataset.name || '';
  document.getElementById('item-price').value = btn.dataset.price || '';
  document.getElementById('item-description').value = btn.dataset.description || '';
  document.getElementById('item-featured').checked = btn.dataset.featured === 'true';
  document.getElementById('item-type-hidden').value = btn.dataset.type || 'service';

  document.getElementById('item-modal').classList.add('open');
}

function closeModal(id) {
  const el = document.getElementById(id);
  if (el) el.classList.remove('open');
}

/* =====================
   ADMIN INIT
   ===================== */
document.addEventListener('DOMContentLoaded', () => {
  // Mobile sidebar toggle
  const toggle = document.getElementById('sidebar-toggle');
  if (toggle) {
    toggle.addEventListener('click', () => {
      document.getElementById('admin-sidebar')?.classList.toggle('open');
    });
  }
  if (window.innerWidth <= 768 && toggle) toggle.style.display = 'block';

  // Panel nav (dashboard / bookings / sales)
  document.querySelectorAll('.admin-nav a[data-panel]').forEach(a => {
    a.addEventListener('click', (e) => {
      e.preventDefault();
      showPanel(a.dataset.panel);
    });
  });
  if (document.getElementById('panel-dashboard')) showPanel('dashboard');

  // Booking search
  const bSearch = document.getElementById('booking-search');
  if (bSearch) bSearch.addEventListener('input', () => filterBookings(bSearch.value));

  // Modal backdrop click to close
  document.querySelectorAll('.modal-overlay').forEach(m => {
    m.addEventListener('click', e => { if (e.target === m) m.classList.remove('open'); });
  });

  // Set admin topbar date if present
  const dateEl = document.getElementById('admin-date');
  if (dateEl) {
    dateEl.textContent = new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
  }
});
