/* =========================================================
   TechKruti – Frontend Application Logic
   Communicates with the Java Spring Boot backend at /api
   ========================================================= */

const API_BASE = '/api';

/* ── State ── */
let workers = [];
let filteredWorkers = [];
let activeCategory = 'all';
let selectedWorker = null;

/* ── DOM refs ── */
const workersGrid    = document.getElementById('workersGrid');
const loader         = document.getElementById('loader');
const searchInput    = document.getElementById('searchInput');
const categoryChips  = document.getElementById('categoryChips');

const hireModal      = document.getElementById('hireModal');
const hireForm       = document.getElementById('hireForm');
const closeHireBtn   = document.getElementById('closeHireModal');
const modalWorkerInfo= document.getElementById('modalWorkerInfo');
const submitHireBtn  = document.getElementById('submitHire');

const postJobModal   = document.getElementById('postJobModal');
const postJobForm    = document.getElementById('postJobForm');
const closePostJobBtn= document.getElementById('closePostJobModal');
const btnPostJob     = document.getElementById('btnPostJob');

const toast          = document.getElementById('toast');

/* =========================================================
   Utilities
   ========================================================= */

function showToast(msg, duration = 3000) {
    toast.textContent = msg;
    toast.hidden = false;
    clearTimeout(toast._timer);
    toast._timer = setTimeout(() => { toast.hidden = true; }, duration);
}

function setFieldError(fieldId, errId, msg) {
    const field = document.getElementById(fieldId);
    const err   = document.getElementById(errId);
    if (msg) {
        field.setAttribute('aria-invalid', 'true');
        err.textContent = msg;
        return false;
    }
    field.removeAttribute('aria-invalid');
    err.textContent = '';
    return true;
}

function clearFormErrors(...errIds) {
    errIds.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.textContent = '';
    });
}

function categoryEmoji(cat) {
    const map = { Plumber:'🪠', Electrician:'⚡', Carpenter:'🪚', Painter:'🎨', Cleaner:'🧹', Mechanic:'🔩' };
    return map[cat] || '👷';
}

/* =========================================================
   Fetch workers from backend (falls back to demo data)
   ========================================================= */

async function fetchWorkers() {
    loader.hidden = false;
    workersGrid.innerHTML = '';
    workersGrid.appendChild(loader);

    try {
        const res = await fetch(`${API_BASE}/workers`);
        if (!res.ok) throw new Error('Network response was not ok');
        workers = await res.json();
    } catch (_) {
        /* ── Demo / offline data ── */
        workers = [
            { id:1, name:'Ravi Kumar',    category:'Plumber',     rating:4.7, ratePerHour:350, available:true,  bio:'10+ years fixing pipes, leaks & drainage.' },
            { id:2, name:'Suresh Patil',  category:'Electrician', rating:4.5, ratePerHour:400, available:true,  bio:'Licensed electrician. Wiring, panels & more.' },
            { id:3, name:'Amit Singh',    category:'Carpenter',   rating:4.8, ratePerHour:300, available:false, bio:'Custom furniture, doors & modular kitchens.' },
            { id:4, name:'Priya Desai',   category:'Painter',     rating:4.6, ratePerHour:250, available:true,  bio:'Interior & exterior painting with premium finishes.' },
            { id:5, name:'Neha Joshi',    category:'Cleaner',     rating:4.9, ratePerHour:200, available:true,  bio:'Deep-cleaning, sanitization & move-out cleans.' },
            { id:6, name:'Manoj Yadav',   category:'Mechanic',    rating:4.4, ratePerHour:450, available:true,  bio:'Two-wheeler & four-wheeler repairs at your doorstep.' },
            { id:7, name:'Kiran Reddy',   category:'Plumber',     rating:4.3, ratePerHour:320, available:false, bio:'Specializes in bathroom fittings & water heaters.' },
            { id:8, name:'Deepak Sharma', category:'Electrician', rating:4.7, ratePerHour:380, available:true,  bio:'Smart home installations & inverter servicing.' },
        ];
    }

    applyFilters();
}

/* =========================================================
   Filtering & Rendering
   ========================================================= */

function applyFilters() {
    const q = searchInput.value.trim().toLowerCase();
    filteredWorkers = workers.filter(w => {
        const matchCat  = activeCategory === 'all' || w.category === activeCategory;
        const matchText = !q || w.name.toLowerCase().includes(q) || w.category.toLowerCase().includes(q) || (w.bio || '').toLowerCase().includes(q);
        return matchCat && matchText;
    });
    renderWorkers();
}

function renderWorkers() {
    workersGrid.innerHTML = '';

    if (filteredWorkers.length === 0) {
        workersGrid.innerHTML = `
            <div class="empty-state">
                <div class="empty-state__icon">🔍</div>
                <p>No workers found.<br>Try a different search or category.</p>
            </div>`;
        return;
    }

    filteredWorkers.forEach(w => {
        const card = document.createElement('article');
        card.className = 'worker-card';
        card.setAttribute('aria-label', `${w.name}, ${w.category}`);
        card.innerHTML = `
            <div class="worker-card__header">
                <div class="worker-card__avatar" aria-hidden="true">${categoryEmoji(w.category)}</div>
                <div class="worker-card__info">
                    <div class="worker-card__name">${escHtml(w.name)}</div>
                    <div class="worker-card__category">${escHtml(w.category)}</div>
                </div>
                <div class="worker-card__rating" aria-label="Rating ${w.rating}">
                    ⭐ ${w.rating}
                </div>
            </div>
            <div class="worker-card__body">
                <p>${escHtml(w.bio || '')}</p>
            </div>
            <div class="worker-card__footer">
                <div>
                    <div class="worker-card__rate">₹${w.ratePerHour}<small>/hr</small></div>
                    <span class="badge ${w.available ? 'badge--available' : 'badge--busy'}">
                        ${w.available ? 'Available' : 'Busy'}
                    </span>
                </div>
                <button class="btn-hire" data-id="${w.id}" ${w.available ? '' : 'disabled'}
                    aria-label="Hire ${escHtml(w.name)}">
                    Hire
                </button>
            </div>`;
        workersGrid.appendChild(card);
    });

    /* Hire button listeners */
    workersGrid.querySelectorAll('.btn-hire:not([disabled])').forEach(btn => {
        btn.addEventListener('click', () => openHireModal(parseInt(btn.dataset.id, 10)));
    });
}

function escHtml(str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

/* =========================================================
   Category Chips
   ========================================================= */

categoryChips.addEventListener('click', e => {
    const chip = e.target.closest('.chip');
    if (!chip) return;
    categoryChips.querySelectorAll('.chip').forEach(c => c.classList.remove('chip--active'));
    chip.classList.add('chip--active');
    activeCategory = chip.dataset.cat;
    applyFilters();
});

/* =========================================================
   Search
   ========================================================= */

searchInput.addEventListener('input', applyFilters);

/* =========================================================
   Hire Modal
   ========================================================= */

function openHireModal(workerId) {
    selectedWorker = workers.find(w => w.id === workerId);
    if (!selectedWorker) return;

    modalWorkerInfo.innerHTML = `
        <div class="worker-card__avatar" aria-hidden="true" style="width:40px;height:40px;font-size:1.3rem;">
            ${categoryEmoji(selectedWorker.category)}
        </div>
        <div>
            <div class="mwi-name">${escHtml(selectedWorker.name)}</div>
            <div class="mwi-cat">${escHtml(selectedWorker.category)}</div>
        </div>
        <div class="mwi-rate">₹${selectedWorker.ratePerHour}/hr</div>`;

    /* Set minimum date to today */
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('scheduledDate').min = today;
    document.getElementById('scheduledDate').value = '';

    hireForm.reset();
    clearFormErrors('errClientName','errClientPhone','errJobDescription','errScheduledDate');
    hireModal.hidden = false;
    document.getElementById('clientName').focus();
}

closeHireBtn.addEventListener('click', () => { hireModal.hidden = true; });
hireModal.addEventListener('click', e => { if (e.target === hireModal) hireModal.hidden = true; });

hireForm.addEventListener('submit', async e => {
    e.preventDefault();
    let valid = true;

    const clientName     = document.getElementById('clientName').value.trim();
    const clientPhone    = document.getElementById('clientPhone').value.trim();
    const jobDescription = document.getElementById('jobDescription').value.trim();
    const scheduledDate  = document.getElementById('scheduledDate').value;

    valid = setFieldError('clientName',     'errClientName',     clientName     ? '' : 'Name is required.') && valid;
    valid = setFieldError('clientPhone',    'errClientPhone',    clientPhone    ? '' : 'Phone number is required.') && valid;
    valid = setFieldError('jobDescription', 'errJobDescription', jobDescription ? '' : 'Please describe the job.') && valid;
    valid = setFieldError('scheduledDate',  'errScheduledDate',  scheduledDate  ? '' : 'Please choose a date.') && valid;

    if (!valid) return;

    submitHireBtn.disabled = true;
    submitHireBtn.textContent = 'Submitting…';

    const payload = {
        workerId:     selectedWorker.id,
        workerName:   selectedWorker.name,
        clientName,
        clientPhone,
        jobDescription,
        scheduledDate,
    };

    try {
        const res = await fetch(`${API_BASE}/hire`, {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify(payload),
        });
        if (!res.ok) throw new Error('Server error');
        hireModal.hidden = true;
        hireForm.reset();
        showToast(`✅ Hired ${selectedWorker.name} successfully!`);
    } catch (_) {
        showToast('❌ Could not submit request. Please try again.');
    } finally {
        submitHireBtn.disabled = false;
        submitHireBtn.textContent = 'Confirm Hire';
    }
});

/* =========================================================
   Post Job Modal
   ========================================================= */

btnPostJob.addEventListener('click', () => {
    postJobForm.reset();
    clearFormErrors('errPjCategory','errPjDescription','errPjDate');
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('pjDate').min = today;
    postJobModal.hidden = false;
    document.getElementById('pjCategory').focus();
});

closePostJobBtn.addEventListener('click', () => { postJobModal.hidden = true; });
postJobModal.addEventListener('click', e => { if (e.target === postJobModal) postJobModal.hidden = true; });

postJobForm.addEventListener('submit', async e => {
    e.preventDefault();
    let valid = true;

    const category    = document.getElementById('pjCategory').value;
    const description = document.getElementById('pjDescription').value.trim();
    const budget      = document.getElementById('pjBudget').value;
    const dateNeeded  = document.getElementById('pjDate').value;

    valid = setFieldError('pjCategory',    'errPjCategory',    category    ? '' : 'Please select a category.') && valid;
    valid = setFieldError('pjDescription', 'errPjDescription', description ? '' : 'Please describe the job.') && valid;
    valid = setFieldError('pjDate',        'errPjDate',        dateNeeded  ? '' : 'Please pick a date.') && valid;

    if (!valid) return;

    const submitBtn = postJobForm.querySelector('[type=submit]');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Posting…';

    const payload = { category, description, budget: budget ? Number(budget) : null, dateNeeded };

    try {
        const res = await fetch(`${API_BASE}/jobs`, {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify(payload),
        });
        if (!res.ok) throw new Error('Server error');
        postJobModal.hidden = true;
        postJobForm.reset();
        showToast('📋 Job posted! Workers will contact you soon.');
    } catch (_) {
        showToast('❌ Could not post job. Please try again.');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = 'Post Job';
    }
});

/* =========================================================
   Keyboard accessibility – close modals on Escape
   ========================================================= */

document.addEventListener('keydown', e => {
    if (e.key === 'Escape') {
        hireModal.hidden    = true;
        postJobModal.hidden = true;
    }
});

/* =========================================================
   Bootstrap
   ========================================================= */

fetchWorkers();
