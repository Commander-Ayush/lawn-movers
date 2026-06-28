/* ============================
   GreenCut Lawn Services
   Shared Frontend JavaScript
   (Thymeleaf / Spring Boot build — no client-side data store.
    All booking + services + plans data now comes from the server.)
   ============================ */

/* =====================
   STICKY SIDEBAR + NAVBAR MOWER ANIMATION
   ===================== */
function initMower() {
  const sidebarEl = document.getElementById('sidebar-mower');
  const navMowerEl = document.getElementById('nav-mower');
  const trail = document.getElementById('mower-trail');
  const docH = () => document.documentElement.scrollHeight - window.innerHeight;

  function update() {
    const pct = docH() > 0 ? window.scrollY / docH() : 0;

    // Vertical sidebar mower
    if (sidebarEl) {
      const minTop = 90;
      const maxTop = window.innerHeight - 140;
      const top = minTop + pct * (maxTop - minTop);
      sidebarEl.style.top = top + 'px';
      if (trail) trail.style.height = (pct * 60) + 'px';
    }

    // Horizontal navbar mower progress
    if (navMowerEl) {
      const percentageValue = (pct * 100).toFixed(2);
      document.documentElement.style.setProperty('--nav-mower-pct', `${percentageValue}%`);
    }
  }

  window.addEventListener('scroll', update, { passive: true });
  update();
}

/* =====================
   NAVBAR (mobile hamburger)
   ===================== */
function initNav() {
  const ham = document.querySelector('.hamburger');
  const links = document.querySelector('.nav-links');
  if (ham && links) {
    ham.addEventListener('click', () => links.classList.toggle('open'));
  }
  // NOTE: which nav link is "active" is now set server-side per template
  // (th:class on the relevant <a>), so no client-side path detection needed.
}

/* =====================
   TOAST
   ===================== */
function showToast(msg, duration = 3500) {
  let t = document.getElementById('gc-toast');
  if (!t) {
    t = document.createElement('div');
    t.id = 'gc-toast';
    t.className = 'toast';
    document.body.appendChild(t);
  }
  t.textContent = msg;
  t.classList.add('show');
  clearTimeout(t._timer);
  t._timer = setTimeout(() => t.classList.remove('show'), duration);
}

/* =====================
   HOME PAGE — animated stat counters
   ===================== */
function initCounters() {
  const els = document.querySelectorAll('.count-up');
  if (!els.length) return;

  const observer = new IntersectionObserver(entries => {
    entries.forEach(e => {
      if (!e.isIntersecting) return;
      const el = e.target;
      const end = parseInt(el.dataset.target, 10);
      let cur = 0;
      const step = Math.ceil(end / 60);
      const t = setInterval(() => {
        cur = Math.min(cur + step, end);
        el.textContent = cur.toLocaleString() + (el.dataset.suffix || '');
        if (cur >= end) clearInterval(t);
      }, 28);
      observer.unobserve(el);
    });
  }, { threshold: 0.5 });

  els.forEach(el => observer.observe(el));
}

/* =====================
   FADE-IN ON SCROLL (used by some pages)
   ===================== */
function initFadeIns() {
  const els = document.querySelectorAll('.fade-in');
  if (!els.length) return;
  const observer = new IntersectionObserver(entries => {
    entries.forEach(e => { if (e.isIntersecting) e.target.classList.add('visible'); });
  }, { threshold: 0.08 });
  els.forEach(el => observer.observe(el));
}

/* =====================
   FAQ ACCORDION (services / booking pages, if using div-based FAQs)
   ===================== */
function toggleFaq(el) {
  el.classList.toggle('open');
}

/* =====================
   INIT
   ===================== */
document.addEventListener('DOMContentLoaded', () => {
  initNav();
  initMower();
  initCounters();
  initFadeIns();

  // If the booking form just got submitted successfully (server sets a flag
  // that renders a hidden trigger element), pop the toast.
  const successFlag = document.getElementById('gc-success-flag');
  if (successFlag) showToast(successFlag.dataset.message || 'Done!');
});
