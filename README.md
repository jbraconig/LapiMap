# LapiMap

This is a mobile project developed for Android, which was created in 2020 as part of a Master's thesis. It has been made open source.

## Project Purpose

LapiMap aims to precisely represent the location of users on an image-based map, such as a school, a company, or any indoor environment. The core idea is to allow users to locate specific offices or classrooms, and then guide them with a route and real-time location updates, utilizing information from nearby Wi-Fi devices.

This project is published for anyone interested in these types of indoor positioning solutions.

## Features

*   **Indoor Map Display:** Visualize custom indoor maps (e.g., floor plans) as images.
*   **User Location Tracking:** Display the user's approximate location on the custom map.
*   **Point of Interest (POI) Search:** Search for specific locations (offices, classrooms, etc.) within the map.
*   **Pathfinding/Navigation:** Calculate and display a path from the user's current location to a selected POI.
*   **Wi-Fi Based Positioning:** Utilizes nearby Wi-Fi access points to determine the user's position.

## How It Works

LapiMap uses a fingerprinting approach for indoor positioning. It relies on pre-collected Wi-Fi signal strength data (RSSI) at known locations within the mapped area. When a user is in the building, the app scans for nearby Wi-Fi networks and compares the observed signal strengths with the stored fingerprints in the Firebase Cloud Firestore database. This comparison allows the application to estimate the user's current position on the map. Pathfinding is then performed on a grid representation of the map.

## Technologies Used

*   **Language:** Kotlin
*   **Database:** Cloud Firestore (Firebase)

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

*   Android Studio (latest stable version recommended)
*   Android SDK (API Level 21 or higher)
*   A Firebase project with Cloud Firestore enabled. You will need to configure your `google-services.json` file in the `app/` directory.
*   Basic understanding of Android development and Kotlin.

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/YOUR_USERNAME/LapiMap.git
    cd LapiMap
    ```
    (Note: Replace `YOUR_USERNAME` with the actual repository owner's username if you fork it, or remove this line if it's already cloned.)

2.  **Open in Android Studio:**
    Open the cloned project in Android Studio.

3.  **Configure Firebase:**
    *   Create a new Firebase project in the Firebase Console.
    *   Add an Android app to your Firebase project.
    *   Download the `google-services.json` file and place it in the `app/` directory of your project.
    *   Set up your Cloud Firestore database rules and structure according to the application's needs (e.g., collections for maps, access points, and fingerprints).

4.  **Build and Run:**
    Sync the project with Gradle files and run the application on an Android emulator or a physical device.

## Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## License

Distributed under the GPL-3.0 License. See `LICENSE` for more information.
