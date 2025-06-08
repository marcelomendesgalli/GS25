/**
 * Custom JavaScript for Escola Clima Monitor
 */

// Global application object
window.EscolaClimaMonitor = {
    // Configuration
    config: {
        refreshInterval: 300000, // 5 minutes
        alertTimeout: 5000, // 5 seconds
        animationDuration: 300
    },

    // Initialize application
    init: function() {
        this.setupEventListeners();
        this.initializeComponents();
        this.startAutoRefresh();
        console.log('Escola Clima Monitor initialized');
    },

    // Setup global event listeners
    setupEventListeners: function() {
        // Auto-dismiss alerts
        this.setupAlertDismissal();
        
        // Form validation
        this.setupFormValidation();
        
        // Loading states
        this.setupLoadingStates();
        
        // Smooth scrolling
        this.setupSmoothScrolling();
        
        // Keyboard shortcuts
        this.setupKeyboardShortcuts();
    },

    // Initialize components
    initializeComponents: function() {
        // Initialize tooltips
        this.initTooltips();
        
        // Initialize modals
        this.initModals();
        
        // Initialize charts (if any)
        this.initCharts();
        
        // Initialize real-time updates
        this.initRealTimeUpdates();
    },

    // Auto-dismiss alerts after timeout
    setupAlertDismissal: function() {
        const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
        alerts.forEach(alert => {
            setTimeout(() => {
                if (alert && alert.parentNode) {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                }
            }, this.config.alertTimeout);
        });
    },

    // Setup form validation
    setupFormValidation: function() {
        const forms = document.querySelectorAll('.needs-validation');
        forms.forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                    
                    // Focus on first invalid field
                    const firstInvalid = form.querySelector(':invalid');
                    if (firstInvalid) {
                        firstInvalid.focus();
                    }
                }
                form.classList.add('was-validated');
            });
        });

        // Real-time validation
        const inputs = document.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            input.addEventListener('blur', () => {
                if (input.form && input.form.classList.contains('was-validated')) {
                    input.checkValidity();
                }
            });
        });
    },

    // Setup loading states for buttons and forms
    setupLoadingStates: function() {
        // Button loading states
        document.addEventListener('click', event => {
            const button = event.target.closest('button[type="submit"], a.btn');
            if (button && !button.disabled) {
                this.setButtonLoading(button);
            }
        });

        // Form submission loading
        document.addEventListener('submit', event => {
            const form = event.target;
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                this.setButtonLoading(submitBtn);
            }
        });
    },

    // Set button loading state
    setButtonLoading: function(button, loading = true) {
        if (loading) {
            button.dataset.originalText = button.innerHTML;
            button.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Carregando...';
            button.disabled = true;
        } else {
            button.innerHTML = button.dataset.originalText || button.innerHTML;
            button.disabled = false;
            delete button.dataset.originalText;
        }
    },

    // Setup smooth scrolling for anchor links
    setupSmoothScrolling: function() {
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function(e) {
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                    e.preventDefault();
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            });
        });
    },

    // Setup keyboard shortcuts
    setupKeyboardShortcuts: function() {
        document.addEventListener('keydown', event => {
            // Ctrl/Cmd + K for search
            if ((event.ctrlKey || event.metaKey) && event.key === 'k') {
                event.preventDefault();
                const searchInput = document.querySelector('input[type="search"], input[name="nome"]');
                if (searchInput) {
                    searchInput.focus();
                }
            }

            // Escape to close modals
            if (event.key === 'Escape') {
                const openModal = document.querySelector('.modal.show');
                if (openModal) {
                    const modal = bootstrap.Modal.getInstance(openModal);
                    if (modal) modal.hide();
                }
            }
        });
    },

    // Initialize tooltips
    initTooltips: function() {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function(tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    },

    // Initialize modals
    initModals: function() {
        const modalElements = document.querySelectorAll('.modal');
        modalElements.forEach(modalEl => {
            modalEl.addEventListener('shown.bs.modal', () => {
                const firstInput = modalEl.querySelector('input, select, textarea');
                if (firstInput) firstInput.focus();
            });
        });
    },

    // Initialize charts (placeholder for future implementation)
    initCharts: function() {
        // Chart.js or other charting library initialization
        // This would be implemented when adding dashboard charts
        console.log('Charts initialization placeholder');
    },

    // Initialize real-time updates
    initRealTimeUpdates: function() {
        // WebSocket or Server-Sent Events for real-time data
        // This would be implemented for live sensor data updates
        console.log('Real-time updates initialization placeholder');
    },

    // Start auto-refresh for dashboard data
    startAutoRefresh: function() {
        if (window.location.pathname === '/dashboard') {
            setInterval(() => {
                this.refreshDashboardData();
            }, this.config.refreshInterval);
        }
    },

    // Refresh dashboard data
    refreshDashboardData: function() {
        // AJAX call to refresh dashboard statistics
        fetch('/api/dashboard/stats')
            .then(response => response.json())
            .then(data => {
                this.updateDashboardStats(data);
            })
            .catch(error => {
                console.error('Error refreshing dashboard data:', error);
            });
    },

    // Update dashboard statistics
    updateDashboardStats: function(data) {
        // Update statistics cards with new data
        const statsElements = {
            totalEscolas: document.querySelector('[data-stat="total-escolas"]'),
            sensoresAtivos: document.querySelector('[data-stat="sensores-ativos"]'),
            alertasAtivos: document.querySelector('[data-stat="alertas-ativos"]'),
            alertasCriticos: document.querySelector('[data-stat="alertas-criticos"]')
        };

        Object.keys(statsElements).forEach(key => {
            const element = statsElements[key];
            if (element && data[key] !== undefined) {
                this.animateNumber(element, parseInt(element.textContent), data[key]);
            }
        });
    },

    // Animate number changes
    animateNumber: function(element, from, to) {
        const duration = this.config.animationDuration;
        const start = Date.now();
        const timer = setInterval(() => {
            const progress = Math.min((Date.now() - start) / duration, 1);
            const current = Math.floor(from + (to - from) * progress);
            element.textContent = current;
            
            if (progress === 1) {
                clearInterval(timer);
            }
        }, 16); // ~60fps
    },

    // Utility functions
    utils: {
        // Format date
        formatDate: function(date, format = 'dd/MM/yyyy') {
            const d = new Date(date);
            const day = String(d.getDate()).padStart(2, '0');
            const month = String(d.getMonth() + 1).padStart(2, '0');
            const year = d.getFullYear();
            const hours = String(d.getHours()).padStart(2, '0');
            const minutes = String(d.getMinutes()).padStart(2, '0');

            return format
                .replace('dd', day)
                .replace('MM', month)
                .replace('yyyy', year)
                .replace('HH', hours)
                .replace('mm', minutes);
        },

        // Format temperature
        formatTemperature: function(temp) {
            return `${parseFloat(temp).toFixed(1)}°C`;
        },

        // Format humidity
        formatHumidity: function(humidity) {
            return `${parseFloat(humidity).toFixed(1)}%`;
        },

        // Show notification
        showNotification: function(message, type = 'info') {
            const alertDiv = document.createElement('div');
            alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
            alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
            alertDiv.innerHTML = `
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            `;
            
            document.body.appendChild(alertDiv);
            
            // Auto-remove after timeout
            setTimeout(() => {
                if (alertDiv.parentNode) {
                    const bsAlert = new bootstrap.Alert(alertDiv);
                    bsAlert.close();
                }
            }, 5000);
        },

        // Debounce function
        debounce: function(func, wait) {
            let timeout;
            return function executedFunction(...args) {
                const later = () => {
                    clearTimeout(timeout);
                    func(...args);
                };
                clearTimeout(timeout);
                timeout = setTimeout(later, wait);
            };
        },

        // Throttle function
        throttle: function(func, limit) {
            let inThrottle;
            return function() {
                const args = arguments;
                const context = this;
                if (!inThrottle) {
                    func.apply(context, args);
                    inThrottle = true;
                    setTimeout(() => inThrottle = false, limit);
                }
            };
        }
    },

    // API helper functions
    api: {
        // Base API call
        call: function(endpoint, options = {}) {
            const defaultOptions = {
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                }
            };

            return fetch(endpoint, { ...defaultOptions, ...options })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                });
        },

        // Get schools
        getSchools: function(params = {}) {
            const queryString = new URLSearchParams(params).toString();
            return this.call(`/escolas/api?${queryString}`);
        },

        // Get alerts
        getAlerts: function(params = {}) {
            const queryString = new URLSearchParams(params).toString();
            return this.call(`/alertas/api?${queryString}`);
        }
    }
};

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    EscolaClimaMonitor.init();
});

// Handle page visibility changes
document.addEventListener('visibilitychange', function() {
    if (document.hidden) {
        console.log('Page hidden - pausing updates');
    } else {
        console.log('Page visible - resuming updates');
        // Refresh data when page becomes visible again
        if (typeof EscolaClimaMonitor.refreshDashboardData === 'function') {
            EscolaClimaMonitor.refreshDashboardData();
        }
    }
});

// Handle online/offline status
window.addEventListener('online', function() {
    EscolaClimaMonitor.utils.showNotification('Conexão restaurada', 'success');
});

window.addEventListener('offline', function() {
    EscolaClimaMonitor.utils.showNotification('Conexão perdida. Algumas funcionalidades podem não estar disponíveis.', 'warning');
});

// Export for global access
window.ECM = EscolaClimaMonitor;

