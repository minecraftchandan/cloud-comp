
  const container = document.getElementById("container");
  const goRegister = document.getElementById("goRegister");
  const goLogin = document.getElementById("goLogin");
  const signInForm = document.getElementById("signInForm");
  const signUpForm = document.getElementById("signUpForm");
  const placeholder = document.getElementById("formPlaceholder");

  goRegister.addEventListener("click", (e) => {
    e.preventDefault();
    if (window.innerWidth <= 760) {
      signInForm.classList.remove("active");
      signUpForm.classList.add("active");
      placeholder.classList.add("active");
    } else {
      container.classList.add("active");
      placeholder.classList.remove("active");
    }
  });

  goLogin.addEventListener("click", (e) => {
    e.preventDefault();
    if (window.innerWidth <= 760) {
      signUpForm.classList.remove("active");
      signInForm.classList.add("active");
      placeholder.classList.add("active");
    } else {
      container.classList.remove("active");
      placeholder.classList.remove("active");
    }
  });

  const popupEl = document.getElementById("popupMessage");
  const messageEl = document.getElementById("message");
  let messageTimeout = null;

  function showPopupMessage(text) {
    popupEl.textContent = text;
    popupEl.style.display = "block";
    popupEl.style.opacity = 1;
    setTimeout(() => {
      popupEl.style.opacity = 0;
      setTimeout(() => {
        popupEl.style.display = "none";
      }, 500);
    }, 2000);
  }

  function showMessage(text, isError = true, autoHide = true) {
    messageEl.style.display = "block";
    messageEl.textContent = text;
    messageEl.className = isError ? "error-message" : "success-message";
    messageEl.style.opacity = 1;
    if (autoHide) {
      if (messageTimeout) clearTimeout(messageTimeout);
      messageTimeout = setTimeout(() => {
        messageEl.style.opacity = 0;
        setTimeout(() => {
          messageEl.style.display = "none";
        }, 500);
      }, 2500);
    }
  }

  const registerForm = document.getElementById("registerForm");

  const clearFieldErrors = () => {
    ["username", "password", "confirm"].forEach((field) => {
      const input = registerForm[field === "confirm" ? "confirmPassword" : field];
      input.classList.remove("input-error");
      document.getElementById("error-" + field).style.display = "none";
      document.getElementById("error-" + field).textContent = "";
    });
  };

  registerForm.addEventListener("input", clearFieldErrors);

  registerForm.addEventListener("submit", async function (event) {
    event.preventDefault();
    clearFieldErrors();

    const username = registerForm.username.value.trim();
    const password = registerForm.password.value;
    const confirmPassword = registerForm.confirmPassword.value;

    let hasError = false;

    if (!username) {
      showFieldError("username", "Username is required.");
      hasError = true;
    }

    if (!password) {
      showFieldError("password", "Password is required.");
      hasError = true;
    } else if (password.length < 6) {
      showFieldError("password", "Password must be at least 6 characters.");
      hasError = true;
    }

    if (!confirmPassword) {
      showFieldError("confirm", "Please confirm your password.");
      hasError = true;
    } else if (password !== confirmPassword) {
      showFieldError("confirm", "Passwords do not match.");
      hasError = true;
    }

    if (hasError) return;

    try {
      const formData = new URLSearchParams();
      formData.append("username", username);
      formData.append("password", password);
      formData.append("confirmPassword", confirmPassword);

      const response = await fetch("/register", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: formData.toString(),
      });

      const result = await response.json();

      if (result.success) {
        showPopupMessage("Registration successful! Redirecting");
        setTimeout(() => {
          registerForm.reset();
          goLogin.click();
        }, 2000);
      } else {
        showMessage(result.message || "Registration failed.");
      }
    } catch (err) {
      showMessage("An error occurred during registration.");
      console.error(err);
    }
  });

  function showFieldError(field, message) {
    const input = registerForm[field === "confirm" ? "confirmPassword" : field];
    const errorEl = document.getElementById("error-" + field);
    input.classList.add("input-error");
    errorEl.textContent = message;
    errorEl.style.display = "block";
  }

  document.addEventListener("DOMContentLoaded", () => {
    const springError = document.getElementById("spring-error");
    if (springError) {
      setTimeout(() => {
        springError.style.transition = "opacity 0.5s ease";
        springError.style.opacity = 0;
        setTimeout(() => {
          springError.style.display = "none";
        }, 500);
      }, 1500);
    }
  });