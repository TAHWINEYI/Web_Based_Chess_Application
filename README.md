# White Hat Chess App

Welcome to the White Hat Chess App! This project is a chess application built with Spring Boot that supports various features such as move validation, file-based move imports, pawn promotion, and game resetting.

---

## Project Overview

The White Hat Chess App is designed to provide users with an interactive chess-playing experience. It features a web-based UI powered by Thymeleaf and backend logic to validate and manage chess moves according to standard chess rules.

---

## Prerequisites

To run this application, ensure you have the following installed on your system:

* Java 24 or higher
* Maven 3.8+

---

## Features

### ChessController

The `ChessController` handles all chess-related operations and manages interactions between the frontend and backend.

#### Endpoints

1. **GET /api/v1/chess/board**

   * Displays the current state of the chessboard and the active player.
   * Response: A rendered HTML view.

2. **POST /api/v1/chess/move**

   * Processes a player's move.
   * Request: A string representing the move (e.g., `e2e4`).
   * Response: Redirects to the chessboard view.

3. **POST /api/v1/chess/reset**

   * Resets the chess game to its initial state.
   * Response: Redirects to the chessboard view.

4. **POST /api/v1/chess/load-file**

   * Loads and executes a series of moves from an uploaded file.
   * Request: A file containing valid chess moves.
   * Response: Success or error message displayed on the chessboard view.

5. **POST /api/v1/chess/promote**

   * Promotes a pawn to a specified piece type.
   * Request: Position (e.g., `e8`) and piece type (`QUEEN`, `ROOK`, `BISHOP`, `KNIGHT`).
   * Response: Success or error message displayed on the chessboard view.

---

## Technologies Used

* **Spring Boot 3.4.4**: Framework for building Java applications.
* **Thymeleaf**: Template engine for rendering views.
* **Spring MVC**: For managing web requests and responses.
* **Maven**: Build tool for managing dependencies and project structure.

---

## Getting Started

1. Clone the repository:

   ```bash
   git clone https://github.com/username/white-hat-chess.git
   ```
2. Navigate to the project directory:

   ```bash
   cd white-hat-chess
   ```
3. Build the project:

   ```bash
   mvn clean install
   ```
4. Run the application:

   ```bash
   mvn spring-boot:run
   ```
5. Access the application at [http://localhost:8080/api/v1/chess/board](http://localhost:8080/api/v1/chess/board).

---

## Contributing

Feel free to fork this repository and submit pull requests. Please ensure your code adheres to the project’s style and includes relevant tests.

---

## License

This project is licensed under the MIT License. See the LICENSE file for details.

