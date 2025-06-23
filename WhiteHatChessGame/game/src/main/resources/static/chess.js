document.addEventListener('DOMContentLoaded', function() {
    // Initialize board
    initChessBoard();

    // Form submission
    document.getElementById('moveForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const from = document.getElementById('moveFrom').value;
        const to = document.getElementById('moveTo').value;
        const promotion = document.getElementById('promotionGroup').style.display !== 'none'
            ? document.getElementById('promotion').value
            : null;

        submitMove(from + to + (promotion || ''));
    });

    // Clear move button
    document.getElementById('clearMove').addEventListener('click', function() {
        document.getElementById('moveFrom').value = '';
        document.getElementById('moveTo').value = '';
        document.getElementById('promotionGroup').style.display = 'none';
    });

    // Other event listeners...
});

function initChessBoard() {
    const board = document.getElementById('chessBoard');

    // Create coordinate labels
    const files = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];
    const ranks = ['8', '7', '6', '5', '4', '3', '2', '1'];

    // Create board grid
    for (let y = 0; y < 8; y++) {
        const row = document.createElement('div');
        row.className = 'chess-row';

        // Add rank label
        const rankLabel = document.createElement('div');
        rankLabel.className = 'coordinate rank';
        rankLabel.textContent = ranks[y];
        row.appendChild(rankLabel);

        // Create cells
        for (let x = 0; x < 8; x++) {
            const cell = document.createElement('div');
            cell.className = `chess-cell ${(x + y) % 2 === 0 ? 'white-cell' : 'black-cell'}`;
            cell.dataset.x = x;
            cell.dataset.y = y;

            // Add file label to last row
            if (y === 7) {
                const fileLabel = document.createElement('div');
                fileLabel.className = 'coordinate file';
                fileLabel.textContent = files[x];
                cell.appendChild(fileLabel);
            }

            cell.addEventListener('click', handleCellClick);
            row.appendChild(cell);
        }

        board.appendChild(row);
    }
}

// Rest of your JavaScript remains the same...