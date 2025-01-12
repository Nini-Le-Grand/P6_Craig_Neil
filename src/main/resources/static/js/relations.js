function handleInput(inputElement) {
    const emailValue = inputElement.value.trim();
    const submitButton = document.getElementById("relations-btn");

    submitButton.disabled = emailValue === "";
}