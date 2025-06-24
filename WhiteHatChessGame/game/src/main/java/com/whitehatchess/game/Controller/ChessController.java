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
        chessService.makeMove(move, model);
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
        chessService.loadFileMoves(file, model);
        return "redirect:/api/v1/chess/board";
    }

    @PostMapping("/promote")
    public String promotePawn(
            @RequestParam String pos,
            @RequestParam String pieceType,
            Model model) {
        chessService.promotePawns(pos,pieceType,model);
        return "redirect:/api/v1/chess/board";
    }
}