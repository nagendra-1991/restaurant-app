
const menuList = document.getElementById("menu-list");
const foodSelect = document.getElementById("food");
const result = document.getElementById("result");
let menu = [];

async function loadMenu() {
    try {
        const response = await fetch("/api/menu");
        if (!response.ok) throw new Error("Unable to load menu");

        menu = await response.json();
        menuList.replaceChildren();
        foodSelect.replaceChildren();

        menu.forEach(item => {
            const card = document.createElement("article");
            card.className = "food-card";

            const name = document.createElement("h3");
            name.textContent = item.name;

            const description = document.createElement("p");
            description.textContent = item.description;

            const price = document.createElement("p");
            price.className = "price";
            price.textContent = `₹${item.price}`;

            card.append(name, description, price);
            menuList.appendChild(card);

            const option = document.createElement("option");
            option.value = item.name;
            option.textContent = `${item.name} - ₹${item.price}`;
            foodSelect.appendChild(option);
        });
    } catch (error) {
        menuList.textContent = "Could not load menu. Please refresh.";
    }
}

document.getElementById("order-form").addEventListener("submit",
    async event => {
        event.preventDefault();
        result.textContent = "Placing your order...";

        const order = {
            customerName: document.getElementById("customer").value.trim(),
            itemName: foodSelect.value,
            quantity: Number(document.getElementById("quantity").value)
        };

        try {
            const response = await fetch("/api/orders", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(order)
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(data.message || "Order failed");
            }

            result.textContent =
                `${data.message} Order #${data.orderId}. ` +
                `${data.itemName} × ${data.quantity}. ` +
                `Total: ₹${data.total}.`;
        } catch (error) {
            result.textContent = error.message ||
                "Unable to place order. Please try again.";
        }
    }
);

loadMenu();

