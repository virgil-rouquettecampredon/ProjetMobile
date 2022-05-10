package com.example.projetmobile.Model;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.example.projetmobile.GamePlayerOverlayFragment;
import com.example.projetmobile.Model.Mouvement.Movement;
import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Pieces.Bishop;
import com.example.projetmobile.Model.Pieces.Knight;
import com.example.projetmobile.Model.Pieces.Piece;
import com.example.projetmobile.Model.Pieces.Queen;
import com.example.projetmobile.Model.Pieces.Tower;
import com.example.projetmobile.R;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class GameManager {
    protected static final int QUEEN    = 0;
    protected static final int TOWER    = 1;
    protected static final int BISHOP   = 2;
    protected static final int KNIGHT   = 3;

    //For animation control
    public static boolean ANIMATION_FINISH  = true;
    public static boolean ANIMATION_PIECE   = true;

    //For console printing only
    protected static boolean DEBUG_FOR_ONCLK              = false;
    protected static boolean DEBUG_FOR_GAME_LOGIC         = true;
    protected static boolean DEBUG_FOR_GAME_COMPLEXMVT    = false;
    protected static boolean DEBUG_FOR_GAME_ROCK          = true;
    protected static boolean DEBUG_FOR_GAME_MENACE        = false;
    protected static boolean DEBUG_FOR_GAME_ANIMATION     = false;

    //Board of the current game
    protected Board board;
    protected Context context;
    protected List<Player> players;
    protected Player currentPlayer;

    protected List<Position> lastPossiblePositions;
    protected Position lastPosOfPieceSelected;
    protected Position lastPosPreSelected;

    //For player overlay
    protected List<GamePlayerOverlayFragment> playersUI;
    protected List<FrameLayout> playersUI_FrameLayout;
    protected int piece_size = -1;

    protected int nbTurn = 0;

    //For getting all shot from all players
    Deque<List<Shot>> allShots;

    //For stop the onclk behavior
    protected boolean gameStopped;

    //For each piece that is not on an ally team and can menace a victoryCondition piece of the current player
    //Save the case of this Piece to MAJ UI next
    protected List<Position> positionWithDanger;

    //For the rock behavior only
    protected Map<Piece, List<Association_rock>> rockPiecePositons;

    public GameManager(Context context, Board b) {
        this.board = b;
        this.context = context;
        this.players = new ArrayList<>();
        this.lastPossiblePositions = new ArrayList<>();

        this.playersUI = new ArrayList<>();
        this.playersUI_FrameLayout = new ArrayList<>();

        this.allShots = new ArrayDeque<>();
        this.gameStopped = false;

        this.positionWithDanger = new ArrayList<>();
        this.rockPiecePositons = new HashMap<>();
    }


    /**
     * ======== For launch and play a game ========
     **/
    public void start() {
        if (DEBUG_FOR_GAME_LOGIC) System.out.println("GAME MANAGER START");

        //Clear the board
        this.board.clear();

        //Init the board
        this.players = this.board.initGame_players();
        majPlayerLayout();

        if (startANewTurn()) {
            onEndingGame();
        } else {
            computeOnclkListener();
        }
    }

    //Function for complete the main onclick listener of the board app
    public void computeOnclkListener() {
        if (DEBUG_FOR_ONCLK) System.out.println("COMPUTE ONCLK LISTENER");

        //Onclick for the game core
        board.initGame_onclk(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!gameStopped) {
                    if (DEBUG_FOR_ONCLK)
                        System.out.println("<==============> NEW ONCLCK DETECTION <==============>");
                    Case c = (Case) view;
                    if (DEBUG_FOR_ONCLK) System.out.println("ONCLK ON : " + c);
                    Piece p = c.getPiece();
                    Association_rock as = c.get_rock_position();
                    if (DEBUG_FOR_ONCLK) {
                        System.out.println("ONCLK PIECE : " + p);
                        if (p != null) System.out.println("VAL MVT : " + p.isMovedYet());
                        if (as != null) System.out.println(as);
                    }


                    //If the case was selected the round just before by the player
                    if (c.is_pre_selected_pos()) {
                        //Perform the mouvement
                        if (DEBUG_FOR_ONCLK) System.out.println("PERFORM MOUVEMENT");
                        //Clear the preselected positions
                        board.setPossiblePreSelectedPos(lastPosPreSelected.getX(), lastPosPreSelected.getY(), false);

                        /* =========================
                             CLEAR ALL THE POSITIONS
                        ========================*/
                        //Clear all the possibles moves
                        for (Position pos : lastPossiblePositions) {
                            board.setPossiblePos(pos.getX(), pos.getY(), false);
                            board.setPossiblePosRock(pos.getX(), pos.getY(), null);
                        }
                        lastPossiblePositions.clear();

                        //If we select a position with no rock behavior
                        if (as == null) {
                            //We need to perform a variant of the shot with move animation
                            if (ANIMATION_PIECE) {
                                //Animation
                                moveAPiece_animated(lastPosOfPieceSelected, lastPosPreSelected, false);
                                lastPosPreSelected = null;
                            } else {
                                //No animation
                                Piece pMoved = moveAPiece(lastPosOfPieceSelected, lastPosPreSelected, false);

                                //We need to check if the movement need an upgrade treatment
                                if (c.is_end_case() && pMoved.canBeTransformed()) {
                                    //Launch the upgrade treatment
                                    transformAPiece(pMoved, lastPosPreSelected);
                                    lastPosPreSelected = null;
                                } else {
                                    lastPosPreSelected = null;
                                    onFinishedTurn();
                                    //Start a new turn
                                    if (startANewTurn()) {
                                        //If its finished, then stop the treatment
                                        onEndingGame();
                                    }
                                }
                            }
                        } else {
                            //We need to perform a variant of the shot with move animation
                            if (ANIMATION_PIECE) {
                                //Animation
                                moveAPiece_animated_rock(lastPosOfPieceSelected, as);
                                lastPosPreSelected = null;
                            } else {
                                //No animation
                                moveAPiece_rock(lastPosOfPieceSelected, as);
                                lastPosPreSelected = null;
                                onFinishedTurn();
                                //Start a new turn
                                if (startANewTurn()) {
                                    //If its finished, then stop the treatment
                                    onEndingGame();
                                }
                            }
                        }
                    } else {
                        //Clear the last preselected position
                        if (lastPosPreSelected != null) {
                            board.setPossiblePreSelectedPos(lastPosPreSelected.getX(), lastPosPreSelected.getY(), false);
                        }

                        //Then it could be a possible movement case
                        if (c.is_possible_pos()) {
                            //Then this case is transformed on a pre-selected case
                            if (DEBUG_FOR_ONCLK) System.out.println("SET PRESELECTED POSITION");
                            lastPosPreSelected = new Position(c.getColumn(), c.getRow());
                            board.setPossiblePreSelectedPos(lastPosPreSelected.getX(), lastPosPreSelected.getY(), true);
                        } else {
                            //Then this case may just contain a piece on it

                            /* =========================
                                    CLEAR ALL THE POSITIONS
                                ========================*/
                            //Delete precedent position seen
                            for (Position pos : lastPossiblePositions) {
                                board.setPossiblePos(pos.getX(), pos.getY(), false);
                                board.setPossiblePosRock(pos.getX(), pos.getY(), null);
                            }
                            lastPossiblePositions.clear();


                            if (p != null) {
                                //Click on a piece on the board
                                if (DEBUG_FOR_ONCLK) {
                                    System.out.println("CLICK ON A PIECE IN THE BOARD");
                                }
                                //Send new positions player wanted to see
                                List<Position> posPos = currentPlayer.getPositionsPiece(p);
                                for (Position pos : posPos) {
                                    lastPossiblePositions.add(pos);
                                    board.setPossiblePos(pos.getX(), pos.getY(), true);
                                }

                                //Set potentially rock positions
                                List<Association_rock> pos_rock = rockPiecePositons.get(p);
                                if (pos_rock != null) {
                                    for (Association_rock aRock : pos_rock) {
                                        Position pRock = aRock.p1;
                                        lastPossiblePositions.add(pRock);
                                        board.setPossiblePosRock(pRock.getX(), pRock.getY(), aRock);
                                        board.setPossiblePos(pRock.getX(), pRock.getY(), true);
                                    }
                                }

                                //For save an historic of precedents choices
                                lastPosOfPieceSelected = new Position(c.getColumn(), c.getRow());
                            }
                        }
                    }
                    //Draw the changes
                    board.commitChanges();
                }
            }
        });
    }

    //Function for complete the main onclick listener of the board app
    protected boolean startANewTurn() {
        if (DEBUG_FOR_GAME_LOGIC) System.out.println("<===> START A NEW TURN <===>");

        this.currentPlayer = getCurrentPlayer();
        if (DEBUG_FOR_GAME_LOGIC) System.out.println("CUR PLAYER : " + currentPlayer);


        //Maj UI for better understanding in interface of current player
        majLayoutPlayerTurn(this.currentPlayer);

        //Next we can compute the special rock movement
        computeRockInGame(this.currentPlayer);
        if (DEBUG_FOR_GAME_ROCK) {
            System.out.println("RES ROCK");
            for (Piece p : rockPiecePositons.keySet()) {
                System.out.println("ROCK P : " + p);
                for (Association_rock assoc : this.rockPiecePositons.get(p)) {
                    System.out.println("ASSOC : " + assoc);
                }
            }
            System.out.println("RES ROCK END");
        }

        //We create all the movement for all the players
        for (Player p : players) {
            computePossibleMvts(p);
        }
        //And we restrict the current player movements if he is in danger
        performMenaced(this.currentPlayer);

        //We can independently compute the dangerous Case by calculating the possible position for each dangerous enemy neighbour
        performDanger(this.currentPlayer);

        return this.isFinished();
    }

    //=== PLAYERS MANAGEMENT ===
    //Get the ind of a player in the game
    private int getIndex(Player p) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).equals(p)) return i;
        }
        return -1;
    }

    //Get a player who get the Piece piece
    private Player getPlayer(Piece piece) {
        return piece.getPossessor();
    }

    //For getting the current player to play
    public Player getCurrentPlayer() {
        Player p = this.players.get(nbTurn % this.players.size());
        return p;
    }


    /**
     * ======== For game stopping mechanics ========
     **/
    public boolean isFinished() {
        //Detect if current player can still perform at least one movement
        for (Piece p : currentPlayer.getPiecesPlayer()) {
            if (currentPlayer.getPositionsPiece(p).size() > 0) return false;
        }
        return true;
    }

    protected void onEndingGame() {
        if (DEBUG_FOR_GAME_LOGIC) System.out.println("GAME IS FINISHED");

        String mes_start = "";
        String mes_mid = "";
        String mes_end = "";

        //Perform the end of the game
        if (this.isMenaced(this.currentPlayer)) {
            List<Player> playersWin = new ArrayList<>();
            for (Player p : players) {
                if (!p.isAlly(currentPlayer)) {
                    playersWin.add(p);
                }
            }
            if (playersWin.size() == 1) {
                mes_start = this.context.getString(R.string.finish_screen_start);
                mes_mid = playersWin.get(0).getPseudo();
                mes_end = this.context.getString(R.string.finish_screen_winnerOne_end);
            } else {
                mes_start = this.context.getString(R.string.finish_screen_start);

                String res = "";
                for (Player p : playersWin) {
                    res += p.getPseudo() + "-";
                }
                StringBuffer sb = new StringBuffer(res);
                sb.deleteCharAt(sb.length() - 1);

                mes_mid = sb.toString();
                mes_end = this.context.getString(R.string.finish_screen_winnerMany_end);
            }
        } else {
            if (DEBUG_FOR_GAME_LOGIC) System.out.println("EQUALITY");
            mes_start = this.context.getString(R.string.finish_screen_start);
            mes_mid = "";
            mes_end = this.context.getString(R.string.finish_screen_draw_end);
        }
        this.board.onEndOfGame(mes_start, mes_mid, mes_end);
    }


    /**
     * ======== For game menace mechanics ========
     **/
    protected boolean isMenaced(Player playerCurrent) {
        for (Piece piece : playerCurrent.getPiecesPlayer()) {
            if (piece.isVictoryCondition()) {
                for (Player p : players) {
                    if (!playerCurrent.isAlly(p)) {
                        for (Piece pieceEnemy : p.getPiecesPlayer()) {
                            for (Piece eatable : pieceEnemy.getTastyPieces()) {
                                if (eatable == piece) {
                                    //If pieceEnemy menace our victoryPieceCondition
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    //For getting all the piece that menace our victory condition piece
    private void cptMenace(Player playerCurrent) {
        //Res of the search
        for (Piece piece : playerCurrent.getPiecesPlayer()) {
            if (piece.isVictoryCondition()) {
                for (Player p : players) {
                    if (!playerCurrent.isAlly(p)) {
                        for (Piece pieceEnemy : p.getPiecesPlayer()) {
                            for (Piece eatable : pieceEnemy.getTastyPieces()) {
                                if (eatable == piece) {
                                    //If pieceEnemy menace our victoryPieceCondition
                                    positionWithDanger.add(this.board.getPiecePosition(pieceEnemy));
                                    positionWithDanger.add(this.board.getPiecePosition(piece));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    protected void performMenaced(Player p) {
        if (DEBUG_FOR_GAME_MENACE) System.out.println("JE SUIS MENACE");

        //Get all current pos of pieces on the board
        HashMap<Piece, Position> posPiecesStart = new HashMap<>();
        for (Piece piece : p.getPiecesPlayer()) {
            posPiecesStart.put(piece, this.board.getPiecePosition(piece));
        }

        //TODO Sup if no need again
        if (DEBUG_FOR_GAME_MENACE) {
            System.out.println("HASHMAP");
            for (Piece phm : posPiecesStart.keySet()) {
                System.out.println(phm);
                System.out.println(posPiecesStart.get(phm));
            }
        }

        HashMap<Piece, List<Position>> posPieces = new HashMap<>();
        for (Piece piece : p.getPiecesPlayer()) {
            posPieces.put(piece, new ArrayList<>());
            for (Position pos : p.getPositionsPiece(piece)) {
                posPieces.get(piece).add(pos);
            }
        }


        //TODO Sup if no need again
        if (DEBUG_FOR_GAME_MENACE) {
            System.out.println("POSITIONS A TESTER");
            for (Piece piece : posPieces.keySet()) {
                List<Position> positions = posPieces.get(piece);
                System.out.println("PIECE : " + piece);
                if (positions != null) {
                    for (Position pos : positions) {
                        System.out.println("POS : " + pos);
                    }
                }
            }
        }
        if (DEBUG_FOR_GAME_MENACE) System.out.println("<===> START THE TREATMENT");

        for (Piece piece : posPieces.keySet()) {
            List<Position> positions = posPieces.get(piece);
            if (positions != null) {
                Iterator<Position> ite = positions.iterator();
                while (ite.hasNext()) {
                    Position pos = ite.next();
                    if (DEBUG_FOR_GAME_MENACE) {
                        System.out.println("=>CURRENT POS SEEN : " + pos);
                        System.out.println("FROM PIECE : " + piece);
                    }

                    //We perform the movement
                    Position startPos = posPiecesStart.get(piece);
                    if (DEBUG_FOR_GAME_MENACE) System.out.println("START AT : " + startPos);


                    moveAPiece(startPos, pos, true);

                    for (Player gamePlayer : players) {
                        computePossibleMvts(gamePlayer);
                    }

                    //Now check if there is still a chess state
                    if (this.isMenaced(p)) {
                        //Then we can't move this piece to this position
                        //System.out.println("REMOVE IT");
                        if (DEBUG_FOR_GAME_MENACE) System.out.println("    => STILL MENACED");
                        ite.remove();
                    }

                    List<Shot> ls = cancelAShot();
                    if (DEBUG_FOR_GAME_MENACE) {
                        for (Shot s : ls) {
                            System.out.println(s);
                        }
                    }
                }
            }
        }

        if (DEBUG_FOR_GAME_MENACE) {
            System.out.println("POSITIONS RESTANTES");
            for (Piece piece : posPieces.keySet()) {
                List<Position> positions = posPieces.get(piece);
                System.out.println("PIECE : " + piece);
                if (positions != null) {
                    System.out.println("ON A DES POSITIONS");
                    for (Position pos : positions) {
                        System.out.println("POS : " + pos);
                    }
                }
            }
        }

        //Now we can update all the possible move from all players
        for (Player gamePlayer : players) {
            computePossibleMvts(gamePlayer);
        }
        p.resetPossibleMove();
        //And after for all our possible movement calculated before for our menaced player
        for (Piece piece : posPieces.keySet()) {
            List<Position> positions = posPieces.get(piece);
            if (positions != null) {
                p.setPossibleMove(piece, positions);
            }
        }
    }

    protected void performDanger(Player p) {
        //First reset the precedent treatment
        for (Position pos : positionWithDanger) {
            if (board.isGoodPos(pos.getX(), pos.getY())) {
                this.board.setPossibleCaseWithMenaceOnIt(pos.getX(), pos.getY(), false);
            }
        }
        positionWithDanger.clear();
        //Then cpt the new menace
        cptMenace(p);

        if (DEBUG_FOR_GAME_MENACE) {
            System.out.println("PERFORM DANGER");
            for (Position posEn : positionWithDanger) {
                System.out.println(posEn);
            }
        }

        //And finally maj the UI
        for (Position pos : positionWithDanger) {
            if (board.isGoodPos(pos.getX(), pos.getY())) {
                this.board.setPossibleCaseWithMenaceOnIt(pos.getX(), pos.getY(), true);
            }
        }
    }


    /**
     * ======== For movements computation ========
     **/
    //For perform a movement on the board (lightMove = true -> move that will be canceled just after : no UI modif, no anim performed ...)
    protected Piece moveAPiece(Position start, Position end, boolean lightMove) {
        //Move a piece form start position to end position

        //System.out.println("[MOVE A PIECE]");
        Case start_case = board.getACase(start.getX(), start.getY());
        Case end_case = board.getACase(end.getX(), end.getY());

        //System.out.println("FROM THIS PLACE : " + start_case);
        //System.out.println("TO THIS PLACE : " + end_case);

        Piece moved = start_case.getPiece();
        Piece possibly_eaten = end_case.getPiece();

        //System.out.println("PIECE START : " + mooved);
        //System.out.println("PIECE END : " + possibly_eaten);

        allShots.push(Collections.singletonList(new Shot(moved, start, end, possibly_eaten, !lightMove, !moved.isMovedYet())));

        if (possibly_eaten != null) {
            //If there is a  piece at the direction of displacement, its eaten
            Player pEated = eatAPiece(possibly_eaten);

            //We MAJ the cimetary visual after
            if (pEated != null && !lightMove) {
                addCimetaryLayout(pEated, possibly_eaten);
            }
        }
        board.setAPieces(start_case.getColumn(), start_case.getRow(), null);
        board.setAPieces(end_case.getColumn(), end_case.getRow(), moved);
        moved.setMoved(true);
        return moved;
    }

    //For perform a movement with an animation
    protected void moveAPiece_animated(Position start, Position end, boolean lightMove) {
        //Move a piece form start position to end position with animation
        if (DEBUG_FOR_GAME_ANIMATION) System.out.println("[MOVE A PIECE ANIMATION]");

        //Stop the onclk
        gameStopped = true;

        Case start_case = board.getACase(start.getX(), start.getY());
        Case end_case = board.getACase(end.getX(), end.getY());

        Piece moved = start_case.getPiece();
        Piece possibly_eaten = end_case.getPiece();

        if (DEBUG_FOR_GAME_ANIMATION) {
            System.out.println("PIECE START : " + moved);
            System.out.println("PIECE END : " + possibly_eaten);
        }

        Shot s = new Shot(moved, start, end, possibly_eaten, !lightMove, !moved.isMovedYet());
        allShots.push(Collections.singletonList(s));
        if (DEBUG_FOR_GAME_ANIMATION) System.out.println("MOVE : " + s);

        //Animation function to call for perform a smooth displacement of the piece and not a teleportation
        board.animatedDisplacement(new ArrayList<>(Collections.singletonList(start_case)), new ArrayList<>(Collections.singletonList(end_case)), 1, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                board.setAPieces(start_case.getColumn(), start_case.getRow(), null);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (DEBUG_FOR_GAME_ANIMATION) System.out.println("GAMEMANAGER ENDING ANIMATION");
                //Visual animation ending
                board.restart_no_animation_context();

                //All the game ending, and also mechanism to preform another round
                //Piece ate mechanism
                if (possibly_eaten != null) {
                    //If there is a  piece at the direction of displacement, its eaten
                    Player pAte = eatAPiece(possibly_eaten);

                    //We MAJ the cimetary visual after
                    if (pAte != null && !lightMove) {
                        addCimetaryLayout(pAte, possibly_eaten);
                    }
                }

                //Perform the complete movement
                board.setAPieces(end_case.getColumn(), end_case.getRow(), moved);
                moved.setMoved(true);

                if (DEBUG_FOR_GAME_ANIMATION) {
                    System.out.println("ON MODIFI LA VALEUR DE MVT DE : " + moved);
                    System.out.println("VALEUR DE MVT : " + moved.isMovedYet());
                }

                //Cancel pause moment
                gameStopped = false;

                //We need to check if the movement need an upgrade treatment
                if (end_case.is_end_case() && moved.canBeTransformed()) {
                    //Launch the upgrade treatment
                    transformAPiece(moved, end);
                } else {
                    onFinishedTurn();
                    //Else go through a normal treatment
                    //Go through another round and start a new turn
                    if (startANewTurn()) {
                        //If its finished, then stop the treatment
                        board.commitChanges();
                        onEndingGame();
                    }
                }
                board.commitChanges();
            }
        });
    }

    //For canceling the last shot performed on the game
    protected List<Shot> cancelAShot() {
        List<Shot> sList = allShots.pop();
        for (Shot s : sList) {
            //If the last shot was to eat a piece
            if (s.getEatedPiece() != null) {
                Player p = reviveAPiece(s.getEatedPiece());
                //If we also maj cimetary layout
                if (p != null && s.isMajAff()) {
                    popCimetaryLayout(s.getEatedPiece().getPossessor());
                }
            }

            //Cancel the moove
            this.board.setAPieces(s.getStartPos().getX(), s.getStartPos().getY(), s.getPieceConcerned());
            this.board.setAPieces(s.getEndPos().getX(), s.getEndPos().getY(), s.getEatedPiece());
            s.getPieceConcerned().setMoved(!s.isFirstMoove());
        }
        return sList;
    }

    //For perform the in/out piece mechanism on the board
    protected Player eatAPiece(Piece piece) {
        Player p = getPlayer(piece);
        if (p != null) {
            p.killAPiece(piece);
        }
        return p;
    }

    protected Player reviveAPiece(Piece piece) {
        Player p = getPlayer(piece);
        if (p != null) {
            p.reviveAPiece(piece);
        }
        return p;
    }

    //Computing the possible movement that can perform the pieces of the player
    protected void computePossibleMvts(Player p) {
        //First reset all the precedent moves
        p.resetPossibleMove();

        List<Piece> pieces_player = p.getPiecesPlayer();

        for (Piece p_player : pieces_player) {
            //Don't forget to clear that list for reset the menace
            p_player.clearTastyPieces();

            //Compute classic movements
            Position piece_pos = board.getPiecePosition(p_player);
            List<Movement<?>> mvt_pp = p_player.getAllPossibleMvt(piece_pos.getX(), piece_pos.getY());
            List<Position> positions_piece = new ArrayList<>();
            for (Movement<?> m : mvt_pp) {
                positions_piece.addAll(m.getAllPositions(this.board));
            }
            p.setPossibleMove(p_player, positions_piece);
        }
    }

    protected void transformAPiece(Piece t, Position pos) {
        if (DEBUG_FOR_GAME_LOGIC) System.out.println("TRANSFORM A PIECE : " + t);

        gameStopped = true;

        this.board.onChangePieceShape(this.currentPlayer, p -> {
            int id = -1;
            if (p instanceof Tower) {
                id = TOWER;
                this.board.setAPieces(pos.getX(), pos.getY(), new Tower(true, t, p.getAppearances()));
            }
            if (p instanceof Queen) {
                id = QUEEN;
                this.board.setAPieces(pos.getX(), pos.getY(), new Queen(true, t, p.getAppearances()));
            }
            if (p instanceof Knight) {
                id = KNIGHT;
                this.board.setAPieces(pos.getX(), pos.getY(), new Knight(true, t, p.getAppearances()));
            }
            if (p instanceof Bishop) {
                id = BISHOP;
                this.board.setAPieces(pos.getX(), pos.getY(), new Bishop(true, t, p.getAppearances()));
            }

            //TO TEST
            //player piece destruction
            t.getPossessor().destroyAPiece(t);
            //Maj last shot Piece Transformation
            this.allShots.peek().get(0).setIDTransformedPiece(id);

            gameStopped = false;
            this.board.restart_no_screen_view();
            this.board.commitChanges();

            onFinishedTurn();
            //Now we can go to another turn
            //Start a new turn
            if (startANewTurn()) {
                //If its finished, then stop the treatment
                onEndingGame();
            }
            return null;
        });
    }


    /**
     * ======== For game rock mechanics ========
     **/
    protected void computeRockInGame(Player p) {
        //CLEAR first
        rockPiecePositons.clear();
        //Next need to check if p is not menaced
        if (DEBUG_FOR_GAME_ROCK) System.out.println("CPT ROCK IN GAME");

        if (!this.isMenaced(p)) {
            //We need to get all the pieces in case for the rock
            for (Piece p_player : p.getPiecesToRock()) {
                if (DEBUG_FOR_GAME_ROCK) System.out.println("PLAYER PIECE ROCK = " + p_player);

                //If this piece hasn't moved yet and still alive
                if (!p_player.isMovedYet() && p.isAlive(p_player)) {
                    Position pos_p = this.board.getPiecePosition(p_player);
                    List<Association_rock> list_assoc = p.getAssoToRockWithPiece(p_player);
                    for (Association_rock as : list_assoc) {
                        Piece to_rock_with = as.pieceToRockWith;
                        if (DEBUG_FOR_GAME_ROCK)
                            System.out.println("PLAYER PIECE TO ROCK = " + to_rock_with);

                        //If the other piece hasn't moved yet and still alive
                        if (!to_rock_with.isMovedYet() && p.isAlive(to_rock_with)) {
                            Position pos_p_to_rock = as.posPieceToRockWith;
                            if (DEBUG_FOR_GAME_ROCK) {
                                System.out.println(" ->POS DEP : " + pos_p);
                                System.out.println(" ->POS ROCK : " + pos_p_to_rock);
                            }

                            //Now we check if there is no pieces between theme
                            if (this.board.noPiecesBetween(pos_p, pos_p_to_rock)) {
                                //We can perform the rock, so we mention it
                                if (DEBUG_FOR_GAME_ROCK) System.out.println("ROCK IS POSSIBLE");

                                //We finally need to check if this movement performed make us menaced
                                moveAPiece_rock(pos_p, as);

                                for (Player gamePlayer : players) {
                                    computePossibleMvts(gamePlayer);
                                }

                                //Now check if there is still a chess state
                                if (!this.isMenaced(p)) {
                                    if (DEBUG_FOR_GAME_ROCK)
                                        System.out.println("NO CHESS STATE, CAN GET THE POSITION");
                                    List<Association_rock> positions = this.rockPiecePositons.get(p_player);
                                    if (positions != null) {
                                        positions.add(as);
                                    } else {
                                        this.rockPiecePositons.put(p_player, new ArrayList<>(Collections.singletonList(as)));
                                    }
                                }

                                List<Shot> ls = cancelAShot();
                                if (DEBUG_FOR_GAME_ROCK) {
                                    for (Shot s : ls) {
                                        System.out.println("SHOT CANCELED : " + s);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (DEBUG_FOR_GAME_ROCK) System.out.println("CPT ROCK IN GAME END");
    }

    protected void moveAPiece_rock(Position start, Association_rock as) {
        //Move a piece form start position to end position by rock
        Case p1_case_start = board.getACase(start.getX(), start.getY());

        List<Shot> l = new ArrayList<>();
        Piece piece1 = p1_case_start.getPiece();

        l.add(new Shot(piece1, start, as.p1, null, false, true));
        l.add(new Shot(as.pieceToRockWith, as.posPieceToRockWith, as.p2, null, false, true));
        allShots.push(l);

        board.setAPieces(start.getX(), start.getY(), null);
        board.setAPieces(as.p1.getX(), as.p1.getY(), piece1);
        piece1.setMoved(true);
        board.setAPieces(as.posPieceToRockWith.getX(), as.posPieceToRockWith.getY(), null);
        board.setAPieces(as.p2.getX(), as.p2.getY(), as.pieceToRockWith);
        as.pieceToRockWith.setMoved(true);
    }

    protected void moveAPiece_animated_rock(Position start, Association_rock as) {
        //Move a piece form start position to end position with animation
        if (DEBUG_FOR_GAME_ROCK) System.out.println("[MOVE A PIECE ROCK ANIMATION]");

        //Stop the onclk
        gameStopped = true;

        Case p1_case_start = board.getACase(start.getX(), start.getY());
        Case p2_case_start = board.getACase(as.posPieceToRockWith.getX(), as.posPieceToRockWith.getY());

        Case p1_case_end = board.getACase(as.p1.getX(), as.p1.getY());
        Case p2_case_end = board.getACase(as.p2.getX(), as.p2.getY());

        List<Shot> l = new ArrayList<>();
        l.add(new Shot(p1_case_start.getPiece(), start, as.p1, null, true, true));
        l.add(new Shot(as.pieceToRockWith, as.posPieceToRockWith, as.p2, null, true, true));
        allShots.push(l);

        Piece p2 = p2_case_start.getPiece();
        Piece p1 = p1_case_start.getPiece();

        //Animation function to call for perform a smooth displacement of the piece and not a teleportation
        board.animatedDisplacement(new ArrayList<>(Arrays.asList(p1_case_start, p2_case_start)), new ArrayList<>(Arrays.asList(p1_case_end, p2_case_end)), 2, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                board.setAPieces(p1_case_start.getColumn(), p1_case_start.getRow(), null);
                board.setAPieces(p2_case_start.getColumn(), p2_case_start.getRow(), null);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (DEBUG_FOR_GAME_ANIMATION) System.out.println("GAMEMANAGER ENDING ANIMATION");
                //Visual animation ending
                board.restart_no_animation_context();


                //Perform the complete both movement
                board.setAPieces(p1_case_end.getColumn(), p1_case_end.getRow(), p1);
                p1.setMoved(true);
                board.setAPieces(p2_case_end.getColumn(), p2_case_end.getRow(), p2);
                p2.setMoved(true);

                //Cancel pause moment
                gameStopped = false;

                onFinishedTurn();
                //Go through another round
                //Start a new turn
                if (startANewTurn()) {
                    //If its finished, then stop the treatment
                    board.commitChanges();
                    onEndingGame();
                }
                board.commitChanges();
            }
        });
    }


    /**
     * ======== For layout MAJ (UI) ========
     **/
    //Add piece to cimatary player p layout
    private void addCimetaryLayout(Player p, Piece piece) {
        int ind = getIndex(p);
        if (ind >= 0) {

            if (piece_size < 0)
                piece_size = this.playersUI.get(ind).getLLDeadPieces().getWidth() / (1 * p.getPiecesPlayer().size());

            ImageView img = new ImageView(this.context);
            img.setImageDrawable(piece.getAppearances());

            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(piece_size, piece_size);
            img.setLayoutParams(ll);

            this.playersUI.get(ind).getLLDeadPieces().addView(img);
            this.playersUI.get(ind).getLLDeadPieces().requestLayout();
        }
    }

    //Remove last elem from cimatary player p layout
    private void popCimetaryLayout(Player p) {
        int ind = getIndex(p);
        if (ind >= 0) {
            int childcount = this.playersUI.get(ind).getLLDeadPieces().getChildCount();
            this.playersUI.get(ind).getLLDeadPieces().removeView(this.playersUI.get(ind).getLLDeadPieces().getChildAt(childcount - 1));
        }
    }

    public void majPlayerLayout() {
        for (int i = 0; i < playersUI.size(); i++) {
            Player p = players.get(i);
            this.playersUI.get(i).getTVPseudo().setText(p.getPseudo());
        }
    }

    public void addPlayerInterfaceElement(GamePlayerOverlayFragment frg, FrameLayout frLayout) {
        this.playersUI.add(frg);
        this.playersUI_FrameLayout.add(frLayout);
    }

    private void resetLayoutColors() {
        for (FrameLayout f : playersUI_FrameLayout) {
            f.setBackgroundResource(R.drawable.game_box);
            f.requestLayout();
        }
    }

    protected void majLayoutPlayerTurn(Player p) {
        resetLayoutColors();
        int ind = getIndex(p);
        if (ind >= 0) {
            this.playersUI_FrameLayout.get(ind).setBackgroundResource(R.drawable.game_box_current);
            this.playersUI_FrameLayout.get(ind).requestLayout();
        }
    }


    /**
     * ======== Utilities functions ========
     **/
    //Get Pixel value of dp var (for canva drawing)
    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    //Get Color attribute from reference context (for canva drawing)
    public static int getAttributeColor(Context context, int attributeId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attributeId, typedValue, true);
        int colorRes = typedValue.resourceId;
        int color = -1;
        try {
            color = ContextCompat.getColor(context, colorRes);
        } catch (Resources.NotFoundException e) {
            System.out.println("=====> ERROR : Not found color resource by id: " + colorRes);
        }
        return color;
    }

    public void onFinishedTurn(){
        nbTurn++;
    }
}
