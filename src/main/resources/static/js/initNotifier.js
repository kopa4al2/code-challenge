
const NOTIFICATION_TYPES = {
    PRIMARY: "primary",
    SUCCESS: "success",
    ERROR: 'danger',
    WARNING: 'warning',
    INFO: 'info',
}

const DEFAULT_FADE_TIME = 3000;

window.showNotification = (type, message, fadeAfter = DEFAULT_FADE_TIME) => {
    const alert = document.createElement('div');
    alert.className = `alert alert-${type} show fade`;
    alert.innerHTML = `
            <strong>${message}</strong>
            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>`

    document.querySelector('.notification').append(alert);
    setTimeout(() => {
        $(alert).alert('close');
    }, fadeAfter)
}
