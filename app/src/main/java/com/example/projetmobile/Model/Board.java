package com.example.projetmobile.Model;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.projetmobile.Model.Mouvement.Position;
import com.example.projetmobile.Model.Pieces.Bishop;
import com.example.projetmobile.Model.Pieces.King;
import com.example.projetmobile.Model.Pieces.Knight;
import com.example.projetmobile.Model.Pieces.Pawn;
import com.example.projetmobile.Model.Pieces.Piece;
import com.example.projetmobile.Model.Pieces.Queen;
import com.example.projetmobile.Model.Pieces.Tower;
import com.example.projetmobile.R;

import java.util.ArrayList;
import java.util.List;

public class Board extends TableLayout {
    //private static String log_tag = "BOARD";

    public static ComposedDrawing appearence_possiblepos = new ComposedDrawing();
    public static ComposedDrawing appearence_possiblepos_eat = new ComposedDrawing();
    public static ComposedDrawing appearence_confirmation = new ComposedDrawing();

    private int nb_col;
    private int nb_row;

    private Case[] cases;
    private List<Case> changedCases;

    private Context context;
    private AttributeSet attributeSet;

    private int white_color;
    private int black_color;
    private int selection_color;
    private int eat_color;
    private int confirmation_color;

    public Board(Context context) {
        super(context);
        this.context = context;
        this.attributeSet = null;
        System.out.println("CONSTRUCTEUR");
        changedCases = new ArrayList<>();


        setcolors();
        setdimensions();
        setAppearences();

        this.cases = new Case[nb_col*nb_row];
        initBoard();
        constructXMLBoard();
    }

    public Board(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attributeSet = attrs;
        System.out.println("CONSTRUCTEUR avec ATTRS");
        changedCases = new ArrayList<>();

        setcolors();
        setdimensions();
        setAppearences();

        this.cases = new Case[nb_col*nb_row];
        initBoard();
        constructXMLBoard();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //Measure Width
        int width = widthSize;
        //Measure Height
        int height = heightSize;
        int taille_case = Math.min(width/nb_col,height/nb_row);
        majSizeCases(taille_case);
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean isColumnStretchable(int columnIndex) {
        return false;
    }

    private void setcolors() {
        white_color = GameManager.getAttributeColor(context,R.attr.white_case_color);
        black_color = GameManager.getAttributeColor(context,R.attr.black_case_color);
        selection_color = GameManager.getAttributeColor(context,R.attr.selection_case_color);
        eat_color = GameManager.getAttributeColor(context,R.attr.eat_case_color);
        confirmation_color = GameManager.getAttributeColor(context,R.attr.confirmation_case_color);
    }
    private void setdimensions() {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.Board);
        nb_col = a.getInt(R.styleable.Board_nb_column, 0);
        nb_row = a.getInt(R.styleable.Board_nb_row, 0);
        a.recycle();
    }
    private void setAppearences(){
        appearence_possiblepos.addLayer(ContextCompat.getDrawable(context, R.drawable.board_simple_shape), selection_color);
        appearence_possiblepos_eat.addLayer(ContextCompat.getDrawable(context, R.drawable.board_simple_shape),eat_color);
        appearence_confirmation.addLayer(ContextCompat.getDrawable(context, R.drawable.board_simple_shape),confirmation_color);
    }

    public void majSizeCases(int taille_case){
        for (Case c: cases) {
            c.setDessiredDimention(taille_case);
        }
    }

    public void constructXMLBoard(){
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(tableParams);

        for (int i = 0; i < nb_row; i++) {
            TableRow tableRow = new TableRow(this.context);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < nb_col; j++) {
                tableRow.addView(cases[i*nb_col+j]);
            }
            addView(tableRow);
        }
    }

    //Method called for initializing the board cases
    public void initBoard(){
        //System.out.println("INIT BOARD : " + this);
        //Log.i(log_tag,"INIT BOARD : " + this);
        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(tableParams);
        boolean white;
        boolean start_white = true;

        for (int i = 0; i < nb_row; i++) {
            white = start_white;
            for (int j = 0; j < nb_col; j++) {
                cases[i*nb_col+j] = createCase(j,i, white);
                white = !white;
            }
            start_white = !start_white;
        }
    }

    //Init the game (players and pieces)(called by the manager)
    public List<Player> initGame_players(){
        System.out.println("GAME MANAGER INIT");
        List<Player> players = new ArrayList<>();

        Player p1 = new Player("JOJO");
        Player p2 = new Player("JORDAN");
        players.add(p1);
        players.add(p2);
        Piece p1_pawn1 = new Pawn(p1,this.context, Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p1_pawn2 = new Pawn(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p1_pawn3 = new Pawn(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p1_pawn4 = new Pawn(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p1_pawn5 = new Pawn(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p1_pawn6 = new Pawn(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p1_pawn7 = new Pawn(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p1_pawn8 = new Pawn(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p1_tower1 = new Tower(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK);
        Piece p1_knight1 = new Knight(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK);
        Piece p1_bishop1 = new Bishop(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK,Color.BLACK);
        Piece p1_king = new King(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK,Color.BLACK);
        Piece p1_queen = new Queen(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK);
        Piece p1_tower2 = new Tower(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK);
        Piece p1_knight2 = new Knight(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK);
        Piece p1_bishop2 = new Bishop(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK,Color.BLACK);
        p1.addPiece(p1_pawn1);
        p1.addPiece(p1_pawn2);
        p1.addPiece(p1_pawn3);
        p1.addPiece(p1_pawn4);
        p1.addPiece(p1_pawn5);
        p1.addPiece(p1_pawn6);
        p1.addPiece(p1_pawn7);
        p1.addPiece(p1_pawn8);
        p1.addPiece(p1_tower1);
        p1.addPiece(p1_knight1);
        p1.addPiece(p1_bishop1);
        p1.addPiece(p1_king);
        p1.addPiece(p1_queen);
        p1.addPiece(p1_tower2);
        p1.addPiece(p1_knight2);
        p1.addPiece(p1_bishop2);
        Piece p2_pawn1 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p2_pawn2 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p2_pawn3 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p2_pawn4 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p2_pawn5 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p2_pawn6 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p2_pawn7 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p2_pawn8 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p2_tower1 = new Tower(p2,this.context,Color.BLACK,Color.WHITE,Color.BLACK);
        Piece p2_knight1 = new Knight(p2,this.context,Color.BLACK,Color.WHITE,Color.BLACK);
        Piece p2_bishop1 = new Bishop(p2,this.context,Color.BLACK,Color.WHITE,Color.BLACK,Color.WHITE);
        Piece p2_king = new King(p2,this.context, Color.BLACK,Color.WHITE,Color.BLACK,Color.BLACK);
        Piece p2_queen = new Queen(p2,this.context,Color.BLACK,Color.WHITE,Color.BLACK);
        Piece p2_tower2 = new Tower(p2,this.context,Color.BLACK,Color.WHITE,Color.BLACK);
        Piece p2_knight2 = new Knight(p2,this.context,Color.BLACK,Color.WHITE,Color.BLACK);
        Piece p2_bishop2 = new Bishop(p2,this.context,Color.BLACK,Color.WHITE,Color.BLACK,Color.WHITE);
        p2.addPiece(p2_pawn1);
        p2.addPiece(p2_pawn2);
        p2.addPiece(p2_pawn3);
        p2.addPiece(p2_pawn4);
        p2.addPiece(p2_pawn5);
        p2.addPiece(p2_pawn6);
        p2.addPiece(p2_pawn7);
        p2.addPiece(p2_pawn8);
        p2.addPiece(p2_tower1);
        p2.addPiece(p2_knight1);
        p2.addPiece(p2_bishop1);
        p2.addPiece(p2_king);
        p2.addPiece(p2_queen);
        p2.addPiece(p2_tower2);
        p2.addPiece(p2_knight2);
        p2.addPiece(p2_bishop2);

        //Put all the pieces in the board
        setAPieces(0,1,p1_pawn1);
        setAPieces(1,1,p1_pawn2);
        setAPieces(2,1,p1_pawn3);
        setAPieces(3,1,p1_pawn4);
        setAPieces(4,1,p1_pawn5);
        setAPieces(5,1,p1_pawn6);
        setAPieces(6,1,p1_pawn7);
        setAPieces(7,1,p1_pawn8);

        setAPieces(0,0,p1_tower1);
        setAPieces(1,0,p1_knight1);
        setAPieces(2,0,p1_bishop1);
        setAPieces(3,0,p1_queen);
        setAPieces(4,0,p1_king);
        setAPieces(5,0,p1_knight2);
        setAPieces(6,0,p1_bishop2);
        setAPieces(7,0,p1_tower2);

        setAPieces(0,6,p2_pawn1);
        setAPieces(1,6,p2_pawn2);
        setAPieces(2,6,p2_pawn3);
        setAPieces(3,6,p2_pawn4);
        setAPieces(4,6,p2_pawn5);
        setAPieces(5,6,p2_pawn6);
        setAPieces(6,6,p2_pawn7);
        setAPieces(7,6,p2_pawn8);

        setAPieces(0,7,p2_tower1);
        setAPieces(1,7,p2_knight1);
        setAPieces(2,7,p2_bishop1);
        setAPieces(3,7,p2_queen);
        setAPieces(4,7,p2_king);
        setAPieces(5,7,p2_knight2);
        setAPieces(6,7,p2_bishop2);
        setAPieces(7,7,p2_tower2);
        //Commit changes for displaying
        commitChanges();
        return players;
    }

    //Init the game (onclick cases event)(called by the manager)
    public void initGame_onclk(OnClickListener clk){
        for (Case c: cases) {
            c.setOnClickListener(clk);
        }
    }

    //Method called when you want to apply changes
    public void commitChanges(){
        //System.out.println("COMMIT CHANGES ON BOARD");
        for (Case c: changedCases) {
            c.commit_changes();
        }
        changedCases.clear();
    }

    /**Basic methods for create changing on the board**/
    //Set a piece to the position in param
    //Assuming this position to be correct
    public void setAPieces(int col, int row, @Nullable Piece p){
        //System.out.println("BOARD : SET A PIECE ON : (" +col+","+row+") : " + p);
        //Log.i(log_tag,"BOARD : SET A PIECE ON : (" +col+","+row+") : " + p);
        Case caseInCase = cases[col+row*nb_col];
        if(!changedCases.contains(caseInCase)){
            changedCases.add(caseInCase);
        }
        caseInCase.setPiece(p);
    }

    //Set a possible position (true or false)
    //Assuming this position to be correct
    public void setPossiblePos(int col, int row, boolean pos){
        //System.out.println("BOARD : SET A POSSIBLE POS ON : (" +col+","+row+") : " + pos);
        //Log.i(log_tag,"BOARD : SET A POSSIBLE POS ON : (" +col+","+row+") : " + pos);
        Case caseInCase = cases[col+row*nb_col];
        if(!changedCases.contains(caseInCase)){
            changedCases.add(caseInCase);
        }
        caseInCase.setPossible_pos(pos);
    }

    //Set a possible pre selected position (true or false)
    //Assuming this position to be correct
    public void setPossiblePreSelectedPos(int col, int row, boolean pos){
        //Log.i(log_tag,"BOARD : SET A PRESELECTED POS ON : (" +col+","+row+") : " + pos);
        Case caseInCase = cases[col+row*nb_col];
        if(!changedCases.contains(caseInCase)){
            changedCases.add(caseInCase);
        }
        caseInCase.setPre_selected_pos(pos);
    }

    private Case createCase(int col, int row, boolean isWhite){
        TableRow.LayoutParams layout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        Case case_tab = new Case(context,null);
        case_tab.setLayoutParams(layout);
        case_tab.setColumn(col);
        case_tab.setRow(row);
        case_tab.setWhite(isWhite);
        case_tab.setColor_white(white_color);
        case_tab.setColor_black(black_color);
        return case_tab;
    }

    //Method called for define if a position for a moove on the board is correct or not
    public boolean isGoodPos(int x, int y){
        return ((x>=0 && x<nb_col) && (y>=0 && y<nb_row));
    }

    public Case getACase(int x, int y){
        if(isGoodPos(x,y)) return cases[y*nb_col+x];
        return null;
    }

    //Get a piece position on the board, P(-1,-1) instead
    public Position getPiecePosition(Piece p){
        Position pos = new Position(-1,-1);
        for (Case c: cases) {
            Piece pc = c.getPiece();
            if(pc != null && pc.equals(p)){
                pos = new Position(c.getColumn(),c.getRow());
            }
        }
        return pos;
    }

    @Override
    public String toString() {
        return "Board{" +
                "nb_col=" + nb_col +
                ", nb_row=" + nb_row +
                ", width=" + getWidth() +
                ", height=" + getHeight() +
                ", white_color=" + white_color +
                ", black_color=" + black_color +
                ", selection_color=" + selection_color +
                '}';
    }
}
