// ================================
// LOGIN
// ================================

const loginForm =
    document.getElementById("loginForm");

if (loginForm) {

    loginForm.addEventListener(
        "submit",
        async function(event) {

            event.preventDefault();

            const email =
                document.getElementById(
                    "loginEmail"
                ).value;

            const password =
                document.getElementById(
                    "loginPassword"
                ).value;

            const body =
                "email=" +
                encodeURIComponent(email) +
                "&password=" +
                encodeURIComponent(password);

            const response =
                await fetch(
                    "/api/login",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/x-www-form-urlencoded"
                        },

                        body: body
                    }
                );

            const data =
                await response.json();

            if (data.success) {

                localStorage.setItem(
                    "name",
                    data.name
                );

                localStorage.setItem(
                    "email",
                    data.email
                );

                window.location.href =
                    "dashboard.html";

            } else {

                document.getElementById(
                    "loginMessage"
                ).innerText =
                    data.message;
            }
        }
    );
}


// ================================
// REGISTER
// ================================

const registerForm =
    document.getElementById(
        "registerForm"
    );

if (registerForm) {

    registerForm.addEventListener(
        "submit",
        async function(event) {

            event.preventDefault();

            const name =
                document.getElementById(
                    "registerName"
                ).value;

            const email =
                document.getElementById(
                    "registerEmail"
                ).value;

            const password =
                document.getElementById(
                    "registerPassword"
                ).value;

            const body =
                "name=" +
                encodeURIComponent(name) +
                "&email=" +
                encodeURIComponent(email) +
                "&password=" +
                encodeURIComponent(password);

            const response =
                await fetch(
                    "/api/register",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/x-www-form-urlencoded"
                        },

                        body: body
                    }
                );

            const data =
                await response.json();

            document.getElementById(
                "registerMessage"
            ).innerText =
                data.message;

            if (data.success) {

                setTimeout(
                    function() {

                        window.location.href =
                            "index.html";

                    },
                    1000
                );
            }
        }
    );
}


// ================================
// DASHBOARD
// ================================

const dashboardName =
    document.getElementById(
        "dashboardName"
    );

if (dashboardName) {

    dashboardName.innerText =
        localStorage.getItem(
            "name"
        ) || "Traveler";
}


// ================================
// DESTINATIONS
// ================================

const placesContainer =
    document.getElementById(
        "placesContainer"
    );

if (placesContainer) {

    fetch("/api/destinations")

        .then(
            response =>
                response.json()
        )

        .then(
            destinations => {

                destinations.forEach(
                    place => {

                        const card =
                            document.createElement(
                                "div"
                            );

                        card.className =
                            "place-card";

                        card.innerHTML = `

                            <h2>
                                ${place.name}
                            </h2>

                            <p>
                                <strong>
                                    Location:
                                </strong>
                                ${place.location}
                            </p>

                            <p>
                                ${place.description}
                            </p>

                            <p>
                                <strong>
                                    Distance:
                                </strong>
                                ${place.distance}
                                km
                            </p>

                            <p>
                                <strong>
                                    Daily Cost:
                                </strong>
                                ₹${place.dailyCost}
                            </p>

                            <label>
                                Number of Days
                            </label>

                            <input
                                type="number"
                                min="1"
                                value="3"
                                class="days-input"
                                id="days-${place.id}"
                            >

                            <button
                                onclick="
                                calculateCost(
                                    ${place.id},
                                    ${place.dailyCost}
                                )">

                                Calculate Trip

                            </button>

                            <div
                                class="result"
                                id="result-${place.id}">
                            </div>
                        `;

                        placesContainer.appendChild(
                            card
                        );
                    }
                );
            }
        );
}


// ================================
// CALCULATE TRIP COST
// ================================

function calculateCost(
    id,
    dailyCost
) {

    const days =
        Number(
            document.getElementById(
                `days-${id}`
            ).value
        );

    if (days <= 0) {

        alert(
            "Please enter valid number of days."
        );

        return;
    }

    const total =
        dailyCost * days;

    document.getElementById(
        `result-${id}`
    ).innerHTML = `

        <strong>
            Trip Summary
        </strong>

        <br><br>

        Duration:
        ${days} days

        <br>

        Daily Cost:
        ₹${dailyCost}

        <br>

        <strong>
            Estimated Cost:
            ₹${total}
        </strong>

    `;
}


// ================================
// PROFILE
// ================================

const profileName =
    document.getElementById(
        "profileName"
    );

if (profileName) {

    profileName.innerText =
        localStorage.getItem(
            "name"
        ) || "Not available";

    document.getElementById(
        "profileEmail"
    ).innerText =
        localStorage.getItem(
            "email"
        ) || "Not available";
}


// ================================
// LOGOUT
// ================================

function logout() {

    localStorage.clear();

    window.location.href =
        "index.html";
}