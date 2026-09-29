const API = "/api";

document.addEventListener("DOMContentLoaded", function () {
    loadDashboard();
    loadMachines();
    loadUsageLogs();
    loadTasks();
    loadTechnicians();
});


function showSection(sectionId) {

    document.querySelectorAll(".section").forEach(section => {
        section.classList.remove("active");
    });

    document.getElementById(sectionId).classList.add("active");

    if (sectionId === "dashboard") {
        loadDashboard();
    }

    if (sectionId === "machines") {
        loadMachines();
    }

    if (sectionId === "usage") {
        loadUsageLogs();
    }

    if (sectionId === "tasks") {
        loadTasks();
    }

    if (sectionId === "technicians") {
        loadTechnicians();
    }
}


function showMessage(message, success = true) {

    const box = document.getElementById("messageBox");

    box.textContent = message;

    box.className = success
        ? "message-success"
        : "message-error";

    box.style.display = "block";

    setTimeout(() => {
        box.style.display = "none";
    }, 3000);
}


async function getData(url) {

    const response = await fetch(url);

    if (!response.ok) {
        throw new Error("Request failed");
    }

    return response.json();
}


/* =========================
   DASHBOARD
========================= */

async function loadDashboard() {

    try {

        const machines =
            await getData(`${API}/machines`);

        const usageLogs =
            await getData(`${API}/usage-logs`);

        const tasks =
            await getData(`${API}/maintenance-tasks`);

        const technicians =
            await getData(`${API}/technicians`);

        document.getElementById("totalMachines").textContent =
            machines.length;

        document.getElementById("totalUsageLogs").textContent =
            usageLogs.length;

        document.getElementById("totalTasks").textContent =
            tasks.length;

        document.getElementById("totalTechnicians").textContent =
            technicians.length;

    } catch (error) {

        console.error(error);
    }
}


/* =========================
   MACHINES
========================= */

document.getElementById("machineForm").addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const machine = {

            name:
            document.getElementById("machineName").value,

            maintenanceIntervalHours:
                Number(
                    document.getElementById("maintenanceHours").value
                ),

            maintenanceIntervalDays:
                Number(
                    document.getElementById("maintenanceDays").value
                ),

            currentUsageHours: 0,

            active: true
        };

        try {

            const response =
                await fetch(`${API}/machines`, {

                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify(machine)
                });

            if (!response.ok) {
                throw new Error("Failed to add machine");
            }

            showMessage(
                "Machine added successfully."
            );

            this.reset();

            loadMachines();
            loadDashboard();

        } catch (error) {

            showMessage(
                error.message,
                false
            );
        }
    }
);


async function loadMachines() {

    try {

        const machines =
            await getData(`${API}/machines`);

        const table =
            document.getElementById("machineTableBody");

        table.innerHTML = "";

        machines.forEach(machine => {

            const row =
                document.createElement("tr");

            row.innerHTML = `
                <td>${machine.id}</td>

                <td>${machine.name}</td>

                <td>
                    ${machine.currentUsageHours ?? 0}
                </td>

                <td>
                    ${machine.maintenanceIntervalHours}
                </td>

                <td>
                    ${machine.maintenanceIntervalDays}
                </td>

                <td>
                    ${machine.active ? "Yes" : "No"}
                </td>

                <td>

                    <button
                        class="action-button edit-button"
                        onclick="editMachine(${machine.id})">
                        Edit
                    </button>

                    <button
                        class="action-button delete-button"
                        onclick="deleteMachine(${machine.id})">
                        Delete
                    </button>

                </td>
            `;

            table.appendChild(row);
        });

    } catch (error) {

        console.error(error);
    }
}


/* MACHINE EDIT */

async function editMachine(id) {

    try {

        const machine =
            await getData(`${API}/machines/${id}`);

        const name =
            prompt(
                "Machine Name:",
                machine.name
            );

        if (name === null) {
            return;
        }

        const maintenanceHours =
            prompt(
                "Maintenance Interval Hours:",
                machine.maintenanceIntervalHours
            );

        if (maintenanceHours === null) {
            return;
        }

        const maintenanceDays =
            prompt(
                "Maintenance Interval Days:",
                machine.maintenanceIntervalDays
            );

        if (maintenanceDays === null) {
            return;
        }

        const active =
            prompt(
                "Active? Enter Yes or No:",
                machine.active ? "Yes" : "No"
            );

        if (active === null) {
            return;
        }

        const updatedMachine = {

            name: name,

            maintenanceIntervalHours:
                Number(maintenanceHours),

            maintenanceIntervalDays:
                Number(maintenanceDays),

            currentUsageHours:
                machine.currentUsageHours ?? 0,

            lastMaintenanceDate:
            machine.lastMaintenanceDate,

            active:
                active.toLowerCase() === "yes"
        };

        const response =
            await fetch(
                `${API}/machines/${id}`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body:
                        JSON.stringify(updatedMachine)
                }
            );

        if (!response.ok) {
            throw new Error(
                "Failed to update machine"
            );
        }

        showMessage(
            "Machine updated successfully."
        );

        loadMachines();
        loadDashboard();

    } catch (error) {

        showMessage(
            error.message,
            false
        );
    }
}


/* MACHINE DELETE */

async function deleteMachine(id) {

    if (!confirm(
        "Are you sure you want to delete this machine?"
    )) {
        return;
    }

    try {

        const response =
            await fetch(
                `${API}/machines/${id}`,
                {
                    method: "DELETE"
                }
            );

        if (!response.ok) {
            throw new Error(
                "Failed to delete machine"
            );
        }

        showMessage(
            "Machine deleted successfully."
        );

        loadMachines();
        loadDashboard();

    } catch (error) {

        showMessage(
            error.message,
            false
        );
    }
}


/* =========================
   USAGE LOGS
========================= */

document.getElementById("usageForm").addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const machineId =
            document.getElementById(
                "usageMachineId"
            ).value;

        const usageLog = {

            usageHours:
                Number(
                    document.getElementById(
                        "usageHours"
                    ).value
                )
        };

        try {

            const response =
                await fetch(
                    `${API}/usage-logs/machine/${machineId}`,
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body:
                            JSON.stringify(usageLog)
                    }
                );

            if (!response.ok) {

                throw new Error(
                    "Failed to add usage log"
                );
            }

            showMessage(
                "Usage log added successfully."
            );

            this.reset();

            loadUsageLogs();
            loadMachines();
            loadDashboard();

        } catch (error) {

            showMessage(
                error.message,
                false
            );
        }
    }
);


async function loadUsageLogs() {

    try {

        const logs =
            await getData(
                `${API}/usage-logs`
            );

        const table =
            document.getElementById(
                "usageTableBody"
            );

        table.innerHTML = "";

        logs.forEach(log => {

            const row =
                document.createElement("tr");

            row.innerHTML = `

                <td>
                    ${log.id}
                </td>

                <td>
                    ${log.machine
                ? log.machine.id
                : "-"}
                </td>

                <td>
                    ${log.usageHours}
                </td>

                <td>
                    ${formatDate(log.loggedAt)}
                </td>

                <td>

                    <button
                        class="action-button edit-button"
                        onclick="editUsageLog(${log.id})">
                        Edit
                    </button>

                    <button
                        class="action-button delete-button"
                        onclick="deleteUsageLog(${log.id})">
                        Delete
                    </button>

                </td>
            `;

            table.appendChild(row);
        });

    } catch (error) {

        console.error(error);
    }
}


/* USAGE LOG EDIT */

async function editUsageLog(id) {

    try {

        const log =
            await getData(
                `${API}/usage-logs/${id}`
            );

        const machineId =
            prompt(
                "Machine ID:",
                log.machine
                    ? log.machine.id
                    : ""
            );

        if (machineId === null) {
            return;
        }

        const usageHours =
            prompt(
                "Usage Hours:",
                log.usageHours
            );

        if (usageHours === null) {
            return;
        }

        const updatedLog = {

            usageHours:
                Number(usageHours),

            loggedAt:
            log.loggedAt
        };

        const response =
            await fetch(
                `${API}/usage-logs/${id}?machineId=${machineId}`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(updatedLog)
                }
            );

        if (!response.ok) {

            throw new Error(
                "Failed to update usage log"
            );
        }

        showMessage(
            "Usage log updated successfully."
        );

        loadUsageLogs();
        loadMachines();
        loadDashboard();

    } catch (error) {

        showMessage(
            error.message,
            false
        );
    }
}


/* USAGE LOG DELETE */

async function deleteUsageLog(id) {

    if (!confirm(
        "Are you sure you want to delete this usage log?"
    )) {
        return;
    }

    try {

        const response =
            await fetch(
                `${API}/usage-logs/${id}`,
                {
                    method: "DELETE"
                }
            );

        if (!response.ok) {

            throw new Error(
                "Failed to delete usage log"
            );
        }

        showMessage(
            "Usage log deleted successfully."
        );

        loadUsageLogs();
        loadDashboard();

    } catch (error) {

        showMessage(
            error.message,
            false
        );
    }
}


/* =========================
   MAINTENANCE TASKS
========================= */

document.getElementById("taskForm").addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const machineId =
            document.getElementById(
                "taskMachineId"
            ).value;

        const technicianId =
            document.getElementById(
                "taskTechnicianId"
            ).value;

        const task = {

            taskType:
            document.getElementById(
                "taskType"
            ).value,

            scheduledDate:
                document.getElementById(
                    "scheduledDate"
                ).value
                    ? document.getElementById(
                        "scheduledDate"
                    ).value
                    : null,

            description:
            document.getElementById(
                "taskDescription"
            ).value,

            status: "OPEN"
        };

        let url =
            `${API}/maintenance-tasks/machine/${machineId}`;

        if (technicianId) {

            url +=
                `?technicianId=${technicianId}`;
        }

        try {

            const response =
                await fetch(url, {

                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(task)
                });

            if (!response.ok) {

                throw new Error(
                    "Failed to create maintenance task"
                );
            }

            showMessage(
                "Maintenance task created successfully."
            );

            this.reset();

            loadTasks();
            loadDashboard();

        } catch (error) {

            showMessage(
                error.message,
                false
            );
        }
    }
);


async function loadTasks() {

    try {

        const tasks =
            await getData(
                `${API}/maintenance-tasks`
            );

        const table =
            document.getElementById(
                "taskTableBody"
            );

        table.innerHTML = "";

        tasks.forEach(task => {

            const row =
                document.createElement("tr");

            row.innerHTML = `

                <td>
                    ${task.id}
                </td>

                <td>
                    ${task.machine
                ? task.machine.id
                : "-"}
                </td>

                <td>
                    ${task.technician
                ? task.technician.id
                : "-"}
                </td>

                <td>
                    ${task.taskType}
                </td>

                <td>
                    ${task.status}
                </td>

                <td>
                    ${formatDate(
                task.scheduledDate
            )}
                </td>

                <td>
                    ${task.description ?? "-"}
                </td>

                <td>

                    <button
                        class="action-button edit-button"
                        onclick="editTask(${task.id})">
                        Edit
                    </button>

                    ${
                task.status !== "COMPLETED"
                    ?
                    `
                            <button
                                class="action-button complete-button"
                                onclick="completeTask(${task.id})">
                                Complete
                            </button>
                            `
                    :
                    ""
            }

                    <button
                        class="action-button delete-button"
                        onclick="deleteTask(${task.id})">
                        Delete
                    </button>

                </td>
            `;

            table.appendChild(row);
        });

    } catch (error) {

        console.error(error);
    }
}


/* MAINTENANCE TASK EDIT */

async function editTask(id) {

    try {

        const task =
            await getData(
                `${API}/maintenance-tasks/${id}`
            );

        const taskType =
            prompt(
                "Task Type:",
                task.taskType
            );

        if (taskType === null) {
            return;
        }

        const status =
            prompt(
                "Status (OPEN / COMPLETED):",
                task.status
            );

        if (status === null) {
            return;
        }

        const scheduledDate =
            prompt(
                "Scheduled Date (YYYY-MM-DDTHH:MM:SS):",
                task.scheduledDate
                    ? task.scheduledDate.substring(0, 19)
                    : ""
            );

        if (scheduledDate === null) {
            return;
        }

        const description =
            prompt(
                "Task Description:",
                task.description ?? ""
            );

        if (description === null) {
            return;
        }

        const technicianId =
            prompt(
                "Technician ID (leave blank to keep current):",
                task.technician
                    ? task.technician.id
                    : ""
            );

        if (technicianId === null) {
            return;
        }

        const updatedTask = {

            taskType:
            taskType,

            status:
                status.toUpperCase(),

            scheduledDate:
            scheduledDate,

            description:
            description
        };

        let url =
            `${API}/maintenance-tasks/${id}`;

        if (technicianId.trim() !== "") {

            url +=
                `?technicianId=${technicianId}`;
        }

        const response =
            await fetch(url, {

                method: "PUT",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                body:
                    JSON.stringify(updatedTask)
            });

        if (!response.ok) {

            throw new Error(
                "Failed to update maintenance task"
            );
        }

        showMessage(
            "Maintenance task updated successfully."
        );

        loadTasks();
        loadMachines();
        loadDashboard();

    } catch (error) {

        showMessage(
            error.message,
            false
        );
    }
}


/* COMPLETE TASK */

async function completeTask(id) {

    try {

        const response =
            await fetch(
                `${API}/maintenance-tasks/${id}/complete`,
                {
                    method: "PUT"
                }
            );

        if (!response.ok) {

            throw new Error(
                "Failed to complete task"
            );
        }

        showMessage(
            "Maintenance task completed."
        );

        loadTasks();
        loadMachines();
        loadDashboard();

    } catch (error) {

        showMessage(
            error.message,
            false
        );
    }
}


/* DELETE TASK */

async function deleteTask(id) {

    if (!confirm(
        "Are you sure you want to delete this task?"
    )) {
        return;
    }

    try {

        const response =
            await fetch(
                `${API}/maintenance-tasks/${id}`,
                {
                    method: "DELETE"
                }
            );

        if (!response.ok) {

            throw new Error(
                "Failed to delete task"
            );
        }

        showMessage(
            "Maintenance task deleted successfully."
        );

        loadTasks();
        loadDashboard();

    } catch (error) {

        showMessage(
            error.message,
            false
        );
    }
}


/* =========================
   TECHNICIANS
========================= */

document.getElementById("technicianForm").addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const technician = {

            name:
            document.getElementById(
                "technicianName"
            ).value,

            email:
            document.getElementById(
                "technicianEmail"
            ).value,

            phone:
            document.getElementById(
                "technicianPhone"
            ).value,

            specialization:
            document.getElementById(
                "technicianSpecialization"
            ).value,

            active: true
        };

        try {

            const response =
                await fetch(
                    `${API}/technicians`,
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body:
                            JSON.stringify(technician)
                    }
                );

            if (!response.ok) {

                throw new Error(
                    "Failed to add technician"
                );
            }

            showMessage(
                "Technician added successfully."
            );

            this.reset();

            loadTechnicians();
            loadDashboard();

        } catch (error) {

            showMessage(
                error.message,
                false
            );
        }
    }
);


async function loadTechnicians() {

    try {

        const technicians =
            await getData(
                `${API}/technicians`
            );

        const table =
            document.getElementById(
                "technicianTableBody"
            );

        table.innerHTML = "";

        technicians.forEach(technician => {

            const row =
                document.createElement("tr");

            row.innerHTML = `

                <td>
                    ${technician.id}
                </td>

                <td>
                    ${technician.name}
                </td>

                <td>
                    ${technician.email}
                </td>

                <td>
                    ${technician.phone}
                </td>

                <td>
                    ${technician.specialization}
                </td>

                <td>
                    ${technician.active
                ? "Yes"
                : "No"}
                </td>

                <td>

                    <button
                        class="action-button edit-button"
                        onclick="editTechnician(${technician.id})">
                        Edit
                    </button>

                    <button
                        class="action-button delete-button"
                        onclick="deleteTechnician(${technician.id})">
                        Delete
                    </button>

                </td>
            `;

            table.appendChild(row);
        });

    } catch (error) {

        console.error(error);
    }
}


/* TECHNICIAN EDIT */

async function editTechnician(id) {

    try {

        const technician =
            await getData(
                `${API}/technicians/${id}`
            );

        const name =
            prompt(
                "Technician Name:",
                technician.name
            );

        if (name === null) {
            return;
        }

        const email =
            prompt(
                "Email:",
                technician.email
            );

        if (email === null) {
            return;
        }

        const phone =
            prompt(
                "Phone Number:",
                technician.phone
            );

        if (phone === null) {
            return;
        }

        const specialization =
            prompt(
                "Specialization:",
                technician.specialization
            );

        if (specialization === null) {
            return;
        }

        const active =
            prompt(
                "Active? Enter Yes or No:",
                technician.active
                    ? "Yes"
                    : "No"
            );

        if (active === null) {
            return;
        }

        const updatedTechnician = {

            name:
            name,

            email:
            email,

            phone:
            phone,

            specialization:
            specialization,

            active:
                active.toLowerCase() === "yes"
        };

        const response =
            await fetch(
                `${API}/technicians/${id}`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(
                            updatedTechnician
                        )
                }
            );

        if (!response.ok) {

            throw new Error(
                "Failed to update technician"
            );
        }

        showMessage(
            "Technician updated successfully."
        );

        loadTechnicians();
        loadDashboard();

    } catch (error) {

        showMessage(
            error.message,
            false
        );
    }
}


/* DELETE TECHNICIAN */

async function deleteTechnician(id) {

    if (!confirm(
        "Are you sure you want to delete this technician?"
    )) {
        return;
    }

    try {

        const response =
            await fetch(
                `${API}/technicians/${id}`,
                {
                    method: "DELETE"
                }
            );

        if (!response.ok) {

            throw new Error(
                "Failed to delete technician"
            );
        }

        showMessage(
            "Technician deleted successfully."
        );

        loadTechnicians();
        loadDashboard();

    } catch (error) {

        showMessage(
            error.message,
            false
        );
    }
}


/* =========================
   DATE FORMATTER
========================= */

function formatDate(date) {

    if (!date) {
        return "-";
    }

    return new Date(date).toLocaleString();
}