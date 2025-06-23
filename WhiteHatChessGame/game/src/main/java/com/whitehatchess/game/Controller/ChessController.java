package com.whitehatchess.game.Controller;

import com.whitehatchess.game.Domain.*;
import com.whitehatchess.game.Exceptions.InvalidMoveException;
import com.whitehatchess.game.Service.ChessService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/api/v1/chess")
public class ChessController {
    private Game game;
    private final Board board;
    private final ChessService chessService;

    public ChessController(Game game, Board board, ChessService chessService) {
        this.game = game;
        this.board = board;
        this.chessService = chessService;
    }

    @GetMapping("/board")
    public String index(Model model) {
        model.addAttribute("board", game.getBoard());
        model.addAttribute("currentPlayer", game.getCurrentPlayer());
        return "index";
    }

    @PostMapping("/move")
    public String makeMove(
            @RequestParam String move,
            Model model) {
        try {
            // Fixed coordinate conversion (0-based indexing)
            int startX = move.charAt(0) - 'a';
            int startY = 8 - Character.getNumericValue(move.charAt(1));
            int endX = move.charAt(2) - 'a';
            int endY = 8 - Character.getNumericValue(move.charAt(3));

            ChessMove chessMove = new ChessMove(
                    new Position(startX, startY),
                    new Position(endX, endY)
            );
            game.makeMove(chessMove);
        } catch (Exception e) {
            game.getBoard().setStatusMessage("Invalid move: " + e.getMessage());
        }
        return "redirect:/api/v1/chess/board";
    }

    @PostMapping("/reset")
    public String resetGame() {
        game = new Game(board);
        board.initialize();
        return "redirect:/api/v1/chess/board";
    }

    @PostMapping("/load-file")
    public String loadMovesFromFile(@RequestParam("file") MultipartFile file, Model model) {
        try {
            if (file.isEmpty()) {
                model.addAttribute("error", "Please select a file to upload");
                return "redirect:/api/v1/chess/board";
            }

            // Read file content
            String content = new String(file.getBytes());
            String[] moves = content.split("\\s+"); // Split by whitespace

            // Process each move
            for (String move : moves) {
                if (!move.matches("[a-h][1-8][a-h][1-8]")) {
                    model.addAttribute("error", "Invalid move format in file: " + move);
                    return "redirect:/api/v1/chess/board";
                }

                try {
                    int startX = move.charAt(0) - 'a';
                    int startY = 8 - Character.getNumericValue(move.charAt(1));
                    int endX = move.charAt(2) - 'a';
                    int endY = 8 - Character.getNumericValue(move.charAt(3));

                    ChessMove chessMove = new ChessMove(
                            new Position(startX, startY),
                            new Position(endX, endY)
                    );
                    game.makeMove(chessMove);
                } catch (InvalidMoveException e) {
                    model.addAttribute("error", "Invalid move from file: " + move + " - " + e.getMessage());
                    return "redirect:/api/v1/chess/board";
                }
            }

            model.addAttribute("success", "Moves loaded successfully from file!");
        } catch (IOException e) {
            model.addAttribute("error", "Failed to read file: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Error processing file: " + e.getMessage());
        }

        return "redirect:/api/v1/chess/board";
    }

    @PostMapping("/promote")
    public String promotePawn(
            @RequestParam String pos,
            @RequestParam String pieceType,
            Model model) {

        try {
            // Validate input format
            if (pos == null || pos.length() != 2) {
                throw new IllegalArgumentException("Position must be 2 characters (e.g., 'e8')");
            }
            if (pieceType == null || pieceType.isEmpty()) {
                throw new IllegalArgumentException("Piece type must be specified");
            }

            // Convert position (e.g., "e8" to Position)
            int x = pos.charAt(0) - 'a';
            int y = 8 - Character.getNumericValue(pos.charAt(1));
            if (x < 0 || x > 7 || y < 0 || y > 7) {
                throw new IllegalArgumentException("Invalid position on board");
            }
            Position position = new Position(x, y);

            // Convert piece type string to enum
            PieceType type;
            try {
                type = PieceType.valueOf(pieceType.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid piece type. Use: QUEEN, ROOK, BISHOP, or KNIGHT");
            }

            // Validate promotion piece (cannot promote to king or pawn)
            if (type == PieceType.KING || type == PieceType.PAWN) {
                throw new IllegalArgumentException("Cannot promote to " + type);
            }

            // Attempt promotion
            chessService.promotePawn(position, type);
            model.addAttribute("successMessage", "Pawn promoted to " + type);

        } catch (InvalidMoveException e) {
            model.addAttribute("errorMessage", "Promotion failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Invalid input: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Unexpected error during promotion");
        }

        return "redirect:/api/v1/chess/board";
    }
}