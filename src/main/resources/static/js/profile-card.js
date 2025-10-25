  const userId = "587709425708695552"; // Replace with your real Discord user ID
  const userStatusEl = document.getElementById("userStatus");

  function updateStatus() {
    fetch(`https://api.lanyard.rest/v1/users/${userId}`)
      .then(response => response.json())
      .then(data => {
        const status = data?.data?.discord_status;
        if (!status) return;
        // Update status text
        userStatusEl.textContent = status.charAt(0).toUpperCase() + status.slice(1);
        // Optional: Change color based on status
        const colorMap = {
          online: "limegreen",
          idle: "orange",
          dnd: "red",
          offline: "gray"
        };
        userStatusEl.style.color = colorMap[status] || "gray";
      })
      .catch(error => {
        console.error("Failed to fetch status", error);
        userStatusEl.textContent = "Unknown";
        userStatusEl.style.color = "gray";
      });
  }

  // Initial load
  updateStatus();

  // Optional: Refresh every 20 seconds
  setInterval(updateStatus, 20000);
 // </script>
  //<script>
    // Configuration
const DEFAULT_BEHIND_GRADIENT =
  "radial-gradient(farthest-side circle at var(--pointer-x) var(--pointer-y),hsla(266,100%,90%,var(--card-opacity)) 4%,hsla(266,50%,80%,calc(var(--card-opacity)*0.75)) 10%,hsla(266,25%,70%,calc(var(--card-opacity)*0.5)) 50%,hsla(266,0%,60%,0) 100%),radial-gradient(35% 52% at 55% 20%,#00ffaac4 0%,#073aff00 100%),radial-gradient(100% 100% at 50% 50%,#00c1ffff 1%,#073aff00 76%),conic-gradient(from 124deg at 50% 50%,#c137ffff 0%,#07c6ffff 40%,#07c6ffff 60%,#c137ffff 100%)";

const DEFAULT_INNER_GRADIENT =
  "linear-gradient(145deg,#60496e8c 0%,#71C4FF44 100%)";

const ANIMATION_CONFIG = {
  SMOOTH_DURATION: 600,
  INITIAL_DURATION: 1500,
  INITIAL_X_OFFSET: 70,
  INITIAL_Y_OFFSET: 60,
};

const clamp = (value, min = 0, max = 100) =>
  Math.min(Math.max(value, min), max);

const round = (value, precision = 3) =>
  parseFloat(value.toFixed(precision));

const adjust = (value, fromMin, fromMax, toMin, toMax) =>
  round(toMin + ((toMax - toMin) * (value - fromMin)) / (fromMax - fromMin));

const easeInOutCubic = (x) =>
  x < 0.5
    ? 4 * x * x * x
    : 1 - Math.pow(-2 * x + 2, 3) / 2;

class ProfileCard {
  constructor() {
    this.wrapRef = document.getElementById("profileCardWrapper");
    this.cardRef = document.getElementById("profileCard");
    this.contactTrigger = document.getElementById("contactTrigger");
    this.rafId = null;
    this.init();
  }

  init() {
    this.setCardStyles();
    this.setupEventListeners();
    this.runInitialAnimation();

    // Optional: Contact button click handler
this.contactTrigger?.addEventListener("click", () => {
  // Create modal overlay
  const modal = document.createElement("div");
  modal.style.position = "fixed";
  modal.style.top = "0";
  modal.style.left = "0";
  modal.style.width = "100vw";
  modal.style.height = "100vh";
  modal.style.backgroundColor = "rgba(0, 0, 0, 0.6)";
  modal.style.display = "flex";
  modal.style.justifyContent = "center";
  modal.style.alignItems = "center";
  modal.style.zIndex = "9999";

  // Create modal box
  const box = document.createElement("div");
  box.style.backgroundColor = "#fff";
  box.style.padding = "20px";
  box.style.borderRadius = "20px";
  box.style.textAlign = "center";
  box.style.boxShadow = "0 5px 15px rgba(0,0,0,0.3)";

  box.innerHTML = `
    <h3>Contact Me</h3>

    <a href="https://github.com/minecraftchandan" target="_blank" style="display:inline-block;margin:8px;">
      <img src="https://cdn-icons-png.flaticon.com/512/25/25231.png" alt="GitHub" width="48" height="48" style="border-radius:10px;cursor:pointer;">
    </a>

    <a href="https://discord.com/users/587709425708695552" target="_blank" style="display:inline-block;margin:8px;">
      <img src="https://cdn-icons-png.flaticon.com/512/2111/2111370.png" alt="Discord" width="48" height="48" style="border-radius:10px;cursor:pointer;">
    </a>

    <a href="https://www.youtube.com/@HU_tao5669" target="_blank" style="display:inline-block;margin:8px;">
      <img src="https://cdn-icons-png.flaticon.com/512/1384/1384060.png" alt="YouTube" width="48" height="48" style="border-radius:10px;cursor:pointer;">
    </a>

    <br/><br/>
    <label for="contactMessage" style="font-weight:bold;">Leave a message:</label><br/>
    <textarea id="contactMessage" rows="4" style="width:100%;padding:8px;border-radius:6px;border:1px solid #ccc;" placeholder="Type your message here..."></textarea>
    <br/><br/>
    <button id="sendMessageBtn" style="padding:8px 16px;border:none;border-radius:6px;background-color:#28a745;color:white;cursor:pointer;">
      Send Message
    </button>
    <button id="closeModalBtn" style="padding:8px 16px;margin-left:10px;border:none;border-radius:6px;background-color:#173fb5;color:white;cursor:pointer;">
      Close
    </button>
  `;

  modal.appendChild(box);
  document.body.appendChild(modal);

  // Initialize Firebase if not already initialized
  if (!firebase.apps.length) {
    const firebaseConfig = {
      apiKey: "AIzaSyAf3vcMWpDbBWe5Xn_RMAonO53RHqZ15bQ",
      authDomain: "profile-80e12.firebaseapp.com",
      projectId: "profile-80e12",
      storageBucket: "profile-80e12.appspot.com",
      messagingSenderId: "975400278491",
      appId: "1:975400278491:web:afec2dcca155409ca8effe",
    };
    firebase.initializeApp(firebaseConfig);
  }

  const db = firebase.firestore();

  // Button handlers
  const sendMessageBtn = box.querySelector('#sendMessageBtn');
  const closeModalBtn = box.querySelector('#closeModalBtn');

  sendMessageBtn.onclick = async () => {
    const message = box.querySelector('#contactMessage').value.trim();
    if (!message) {
      alert('⚠️ Please enter a message.');
      return;
    }

    // Fetch username from /api/session
    let username = "Anonymous";
    try {
      const res = await fetch('/api/session');
      const data = await res.json();
      if (data?.username) username = data.username;
    } catch (e) {
      console.warn('Could not fetch session:', e);
    }

    try {
      await db.collection("contact").add({
        username,
        message,
        timestamp: new Date().toISOString()
      });
      const successMsg = document.createElement("p");
      successMsg.textContent = "✅ Message sent!";
      successMsg.style.color = "green";
      successMsg.style.marginTop = "10px";
      box.appendChild(successMsg);

      document.body.removeChild(modal);
    } catch (err) {
      console.error(err);
      alert("🚫 Error sending message.");
    }
  };

  closeModalBtn.onclick = () => {
    document.body.removeChild(modal);
  };
});

  }

  setCardStyles() {
    this.wrapRef.style.setProperty("--behind-gradient", DEFAULT_BEHIND_GRADIENT);
    this.wrapRef.style.setProperty("--inner-gradient", DEFAULT_INNER_GRADIENT);
  }

  updateCardTransform(offsetX, offsetY) {
    const { clientWidth: width, clientHeight: height } = this.cardRef;
    const percentX = clamp((100 / width) * offsetX);
    const percentY = clamp((100 / height) * offsetY);
    const centerX = percentX - 50;
    const centerY = percentY - 50;

    const properties = {
      "--pointer-x": `${percentX}%`,
      "--pointer-y": `${percentY}%`,
      "--background-x": `${adjust(percentX, 0, 100, 35, 65)}%`,
      "--background-y": `${adjust(percentY, 0, 100, 35, 65)}%`,
      "--pointer-from-center": `${clamp(Math.hypot(centerX, centerY) / 50, 0, 1)}`,
      "--pointer-from-top": `${percentY / 100}`,
      "--pointer-from-left": `${percentX / 100}`,
      "--rotate-x": `${round(-(centerX / 10))}deg`,
      "--rotate-y": `${round(centerY / 8)}deg`,
    };

    Object.entries(properties).forEach(([prop, val]) => {
      this.wrapRef.style.setProperty(prop, val);
    });
  }

  createSmoothAnimation(duration, startX, startY) {
    const startTime = performance.now();
    const targetX = this.wrapRef.clientWidth / 2;
    const targetY = this.wrapRef.clientHeight / 2;

    const animate = (currentTime) => {
      const elapsed = currentTime - startTime;
      const progress = clamp(elapsed / duration);
      const eased = easeInOutCubic(progress);
      const currentX = adjust(eased, 0, 1, startX, targetX);
      const currentY = adjust(eased, 0, 1, startY, targetY);

      this.updateCardTransform(currentX, currentY);

      if (progress < 1) {
        this.rafId = requestAnimationFrame(animate);
      }
    };

    this.rafId = requestAnimationFrame(animate);
  }

  cancelAnimation() {
    if (this.rafId) {
      cancelAnimationFrame(this.rafId);
      this.rafId = null;
    }
  }

  handlePointerMove = (event) => {
    const rect = this.cardRef.getBoundingClientRect();
    this.updateCardTransform(event.clientX - rect.left, event.clientY - rect.top);
  };

  handlePointerEnter = () => {
    this.cancelAnimation();
    this.wrapRef.classList.add("active");
    this.cardRef.classList.add("active");
  };

  handlePointerLeave = (event) => {
    this.createSmoothAnimation(
      ANIMATION_CONFIG.SMOOTH_DURATION,
      event.offsetX,
      event.offsetY
    );
    this.wrapRef.classList.remove("active");
    this.cardRef.classList.remove("active");
  };

  setupEventListeners() {
    this.cardRef.addEventListener("pointerenter", this.handlePointerEnter);
    this.cardRef.addEventListener("pointermove", this.handlePointerMove);
    this.cardRef.addEventListener("pointerleave", this.handlePointerLeave);
  }

  runInitialAnimation() {
    const initialX = this.wrapRef.clientWidth - ANIMATION_CONFIG.INITIAL_X_OFFSET;
    const initialY = ANIMATION_CONFIG.INITIAL_Y_OFFSET;
    this.updateCardTransform(initialX, initialY);
    this.createSmoothAnimation(ANIMATION_CONFIG.INITIAL_DURATION, initialX, initialY);
  }
}

// Start the interaction
new ProfileCard();
