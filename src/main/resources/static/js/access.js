function handleSubmitButton() {
    const inputs = Array.from(document.forms[0].elements).filter(input => input.type !== 'hidden');
    const button = document.getElementById('submit-btn');
    const areFieldsFilled = inputs.every(field => field.value.trim() !== '');

    button.disabled = !areFieldsFilled;
}
