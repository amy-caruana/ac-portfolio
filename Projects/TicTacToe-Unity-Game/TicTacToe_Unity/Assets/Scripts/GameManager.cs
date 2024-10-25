using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GameManager : MonoBehaviour
{
    public List<Player> players = new List<Player>();
    public Player currentActivePlayer;
    public CanvsManager canvasManager;
    public BoardPiece[,] BoardMap = new BoardPiece[3, 3];
    public bool stopGame = false;

    // Start is called before the first frame update
    void Start()
    {
        players.Add(new Player() { id = Player.Id.Player1, nickname = "P1", assignedFruit = Fruit.FruitType.Apple });
        players.Add(new Player() { id = Player.Id.Player2, nickname = "P2", assignedFruit = Fruit.FruitType.Strawberry });

        ChangeTopName();
        ChangeActivePlayer();
    }

    private void ChangeTopName()
    {
       canvasManager.ChangeTopNames(players.Find(x => x.id == Player.Id.Player1).nickname, players.Find(x => x.id == Player.Id.Player2).nickname);
    }

    public void ChangeActivePlayer()
    {
        if (currentActivePlayer == null)
            currentActivePlayer = players.Find(x => x.id == Player.Id.Player1); //by default set player1 as active player
        else if (currentActivePlayer.id == Player.Id.Player1)
            currentActivePlayer = players.Find(x => x.id == Player.Id.Player2);
        else if(currentActivePlayer.id == Player.Id.Player2)
            currentActivePlayer = players.Find(x => x.id == Player.Id.Player1);

        //notify CavasManager that player is changed
        canvasManager.ChangeBottomLabel("Player Turn: "+currentActivePlayer.nickname);
    }

    public void SelectBoardPiece(GameObject gameObjBoardPiece)
    {
        if (stopGame)
            return;

        BoardPiece boardPiece = gameObjBoardPiece.GetComponent<BoardPiece>();
       
        if (boardPiece.GetFruit() == Fruit.FruitType.None) //if still empty
        {                 
            //set fruit according to playing player
            boardPiece.SetFruit(currentActivePlayer.assignedFruit);
            ChangeActivePlayer();

            //notify canvas manager to draw updated board
            canvasManager.BoardPaint(gameObjBoardPiece);
            
            //update BoardMap
            BoardMap[boardPiece.row, boardPiece.column] = boardPiece;

            //check winner - This is not implemented yet...found another job with a better salary ;)

            // Check for a winner
            if (CheckForWinner())
            {
                //Handle the game-ending logic (e.g., display a winner message, stop the game, etc.)
                //Debug.Log("Player " + currentActivePlayer.nickname + " wins!");
                ChangeActivePlayer();
                canvasManager.ChangeBottomLabel("Winner: " + currentActivePlayer.nickname);
                stopGame = true;
            }
        }
    }

    private bool CheckForWinner()
    {
        // Check for a win in rows, columns, and diagonals
        if (CheckRows() || CheckColumns() || CheckDiagonals())
        {
            return true; // There is a winner
        }

        return false; // No winner yet
    }

    private bool CheckRows()
    {
        for (int i = 0; i < 3; i++)
        {
            if (CheckRowCol(BoardMap[i, 0], BoardMap[i, 1], BoardMap[i, 2]))
            {
                return true; // Found a winner in the row
            }
        }
        return false;
    }

    private bool CheckColumns()
    {
        for (int i = 0; i < 3; i++)
        {
            if (CheckRowCol(BoardMap[0, i], BoardMap[1, i], BoardMap[2, i]))
            {
                return true; // Found a winner in the column
            }
        }
        return false;
    }

    private bool CheckDiagonals()
    {
        // Check diagonals of the board
        return (CheckRowCol(BoardMap[0, 0], BoardMap[1, 1], BoardMap[2, 2]) ||
                CheckRowCol(BoardMap[0, 2], BoardMap[1, 1], BoardMap[2, 0]));
    }

    private bool CheckRowCol(BoardPiece c1, BoardPiece c2, BoardPiece c3)
    {
        // Helper function to check if three BoardPieces are of the same player
        return (c1 != null && c2 != null && c3 != null &&
                c1.GetFruit() == c2.GetFruit() && c2.GetFruit() == c3.GetFruit());
    }

}
