function handleProfileInput() {
    const profileInputs = Array.from(document.querySelectorAll('#profileForm input')).filter(input => input.type !== 'hidden');
    const profileButton = document.getElementById('profile-btn');
    const areProfileFieldsFilled = profileInputs.some(field => field.value.trim() !== '');

    profileButton.disabled = !areProfileFieldsFilled;
}

function handlePasswordInput() {
    const passwordInputs = Array.from(document.querySelectorAll('#passwordForm input')).filter(input => input.type !== 'hidden');
    const passwordButton = document.getElementById('password-btn');
    const arePasswordFieldsFilled = passwordInputs.every(field => field.value.trim() !== '');

    passwordButton.disabled = !arePasswordFieldsFilled;
}