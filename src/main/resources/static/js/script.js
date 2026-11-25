// Alternar sidebar em telas pequenas
const botaoMenu = document.querySelector('.toggle-btn');
if (botaoMenu) {
    botaoMenu.addEventListener('click', () => {
        document.getElementById('sidebar').classList.toggle('active');
    });
}

// Alternar submenu
function toggleSubmenu(event, element) {
    event.preventDefault();
    element.parentElement.classList.toggle('open');
}

// Pede confirmação antes de ações como excluir e cancelar
document.querySelectorAll('form[data-confirmar]').forEach((form) => {
    form.addEventListener('submit', (e) => {
        if (!window.confirm(form.dataset.confirmar)) {
            e.preventDefault();
        }
    });
});
