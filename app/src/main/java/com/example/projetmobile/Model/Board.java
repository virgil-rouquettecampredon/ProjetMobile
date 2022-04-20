package com.example.projetmobile.Model;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

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
import java.util.function.Function;

public class Board extends TableLayout {
    //For console printing only
    private static boolean DEBUG_FOR_BOARD_LOGIC = false;
    private static boolean DEBUG_FOR_BOARD_ANIMATION = false;


    //=== Appearance for the board case type
    public static ComposedDrawing appearance_possiblepos = new ComposedDrawing();
    public static ComposedDrawing appearance_possiblepos_eat = new ComposedDrawing();
    public static ComposedDrawing appearance_confirmation = new ComposedDrawing();
    public static ComposedDrawing appearance_menaced = new ComposedDrawing();
    public static ComposedDrawing appearance_rock = new ComposedDrawing();

    /** ====== BASIC MODEL DATAS ====== **/
    private int nb_col;
    private int nb_row;

    private Case[] cases;
    private List<Case> changedCases;

    //ViewGroup data
    private Context context;
    private AttributeSet attributeSet;

    //=== For the case color and effect management
    private int white_color;
    private int black_color;
    private int selection_color;
    private int eat_color;
    private int confirmation_color;
    private int menaced_color;
    private int rock_color;

    //=== For the indicator management
    private Paint text_indicator;
    private List<String> horizontalBarIndicator;
    private List<String> verticalBarIndicator;

    private float horizontal_bar_offset_horizontal;
    private float horizontal_bar_addition;
    private float horizontal_bar_offset_vertical;
    private float vertical_bar_offset_vertical;
    private float vertical_bar_offset_horizontal;
    private float vertical_bar_addition;

    private Rect r = new Rect();

    //=== For the finish screen management
    private boolean isFinishScreen;

    private float animationTimingCompletion_background;
    private float animationTimingCompletion_border;

    private Paint paint_background;
    private Paint paint_border_Top;
    private Paint paint_border_Bottom;
    private Paint paint_border_LeftRight;
    private Paint paint_text_finish_start;
    private Paint paint_text_finish_players;
    private Paint paint_text_finish_end;

    private String finishMessage_start;
    private String finishMessage_players;
    private String finishMessage_end;

    private float textSizeFinish_start = 100f;
    private float textSizeFinish_players = 50f;
    private float textSizeFinish_end = 100f;

    private int text_y_start;
    private int text_y_mes;
    private int text_y_end;

    private int borderSize_finish = 50;

    private long border_globalDuration = 700;
    private long text_offsetTime = 200;


    //=== For piece movement animation
    private boolean isPieceMoving;

    private Piece pieceToMove;
    private int left_pieceToMove;
    private int right_pieceToMove;
    private int top_pieceToMove;
    private int bottom_pieceToMove;

    private long piece_globalDuration = 100;

    //=== For the transform piece screen
    private ChangePieceScreen onScreenView;


    /** ======== Constructors of the Viewgroup Board ======== **/
    public Board(Context context) {
        super(context);
        this.context = context;
        this.attributeSet = null;
        if(DEBUG_FOR_BOARD_LOGIC)System.out.println("CONSTRUCTEUR");
        changedCases = new ArrayList<>();

        setColors();
        setDimensions();
        setAppearances();

        this.cases = new Case[nb_col*nb_row];
        initBoard();
        constructXMLBoard();
        isFinishScreen = false;
    }
    public Board(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attributeSet = attrs;
        if(DEBUG_FOR_BOARD_LOGIC)System.out.println("CONSTRUCTEUR avec ATTRS");
        changedCases = new ArrayList<>();

        setColors();
        setDimensions();
        setAppearances();

        this.cases = new Case[nb_col*nb_row];
        initBoard();
        constructXMLBoard();
        isFinishScreen = false;
    }


    /** ======== ViewGroup Methods ======== **/
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //Get paddings values
        int valuePadding = (int) getResources().getDimension(R.dimen.board_margin_max) + (int) getResources().getDimension(R.dimen.board_margin_min);

        //Measure Width
        int width = widthSize - valuePadding;
        //Measure Height
        int height = heightSize - valuePadding;
        int size_case = Math.min(width/nb_col,height/nb_row);

        majSizeCases(size_case);
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //When we need to know the exact current size of the board
        setPaintFinishScreen();
        setPaintIndicatorText();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(DEBUG_FOR_BOARD_LOGIC) System.out.println("DRAW BOARD");

        String aff;
        float off_curX = horizontal_bar_offset_horizontal;
        float off_curY = horizontal_bar_offset_vertical;
        //Draw horizontal bar first
        for (int i = 0; i < nb_col; i++) {
            aff = horizontalBarIndicator.get(i);
            text_indicator.getTextBounds(aff, 0, aff.length(), r);
            float add_y =  + r.height() / 2f - r.bottom;
            canvas.drawText(aff,off_curX,off_curY+add_y,text_indicator);
            off_curX+=horizontal_bar_addition;
        }

        off_curX = vertical_bar_offset_horizontal;
        off_curY = vertical_bar_offset_vertical;
        //Draw vertical bar next
        for (int i = 0; i < nb_row; i++) {
            aff = verticalBarIndicator.get(i);
            text_indicator.getTextBounds(aff, 0, aff.length(), r);
            float add_y =  + r.height() / 2f - r.bottom;
            canvas.drawText(aff,off_curX,off_curY+add_y,text_indicator);
            off_curY+=vertical_bar_addition;
        }
    }
    @Override
    protected void dispatchDraw(Canvas canvas) {
        if(DEBUG_FOR_BOARD_LOGIC) System.out.println("DISPATCH DRAW BOARD");

        //For the drawing of the finish screen
        super.dispatchDraw(canvas);
        if(isFinishScreen) {
            //Draw the background
            canvas.drawRect(0, 0, getWidth(), this.animationTimingCompletion_background * getHeight(), paint_background);

            //Half of the animation
            if (this.animationTimingCompletion_border >= .5) {
                int xPos = (getWidth() / 2);
                canvas.drawText(finishMessage_start, xPos, text_y_start, paint_text_finish_start);
                canvas.drawText(finishMessage_players, xPos, text_y_mes, paint_text_finish_players);
                canvas.drawText(finishMessage_end, xPos, text_y_end, paint_text_finish_end);
            }

            if (this.animationTimingCompletion_border <= .25) {
                //Borders TOP
                canvas.drawRect(
                        getWidth() / 2.0f,
                        -borderSize_finish / 2.0f,
                        (this.animationTimingCompletion_border * (getWidth() + borderSize_finish / 2.0f) * 2.0f) + (getWidth() / 2.0f),
                        borderSize_finish / 2.0f, paint_border_Top);
                canvas.drawRect(
                        (getWidth() / 2.0f) - (this.animationTimingCompletion_border * (getWidth() + borderSize_finish / 2.0f) * 2.0f),
                        -borderSize_finish / 2.0f,
                        getWidth() / 2.0f,
                        borderSize_finish / 2.0f, paint_border_Top);

            } else if (this.animationTimingCompletion_border <= .75) {
                //Borders TOP
                canvas.drawRect(
                        getWidth() / 2.0f,
                        -borderSize_finish / 2.0f,
                        getWidth() + borderSize_finish / 2.0f,
                        +borderSize_finish / 2.0f, paint_border_Top);

                canvas.drawRect(
                        borderSize_finish / 2.0f,
                        -borderSize_finish / 2.0f,
                        getWidth() / 2.0f,
                        borderSize_finish / 2.0f, paint_border_Top);

                //Borders RIGHT and LEFT
                canvas.drawRect(
                        -borderSize_finish / 2.0f,
                        -borderSize_finish / 2.0f,
                        +borderSize_finish / 2.0f,
                        (this.animationTimingCompletion_border - 0.25f) * 2.0f * (getHeight() + borderSize_finish / 2.0f), paint_border_LeftRight);
                canvas.drawRect(
                        getWidth() - borderSize_finish / 2.0f,
                        -borderSize_finish / 2.0f,
                        getWidth() + borderSize_finish / 2.0f,
                        (this.animationTimingCompletion_border - 0.25f) * 2.0f * (getHeight() + borderSize_finish / 2.0f), paint_border_LeftRight);
            } else {
                //Borders TOP
                canvas.drawRect(
                        getWidth() / 2.0f,
                        -borderSize_finish / 2.0f,
                        getWidth() + borderSize_finish / 2.0f,
                        +borderSize_finish / 2.0f, paint_border_Top);

                canvas.drawRect(
                        borderSize_finish / 2.0f,
                        -borderSize_finish / 2.0f,
                        getWidth() / 2.0f,
                        borderSize_finish / 2.0f, paint_border_Top);

                //Borders RIGHT and LEFT
                canvas.drawRect(
                        -borderSize_finish / 2.0f,
                        -borderSize_finish / 2.0f,
                        borderSize_finish / 2.0f,
                        getHeight() + borderSize_finish / 2.0f, paint_border_LeftRight);
                canvas.drawRect(
                        getWidth() - borderSize_finish / 2.0f,
                        -borderSize_finish / 2.0f,
                        getWidth() + borderSize_finish / 2.0f,
                        getHeight() + borderSize_finish / 2.0f, paint_border_LeftRight);

                //Borders BOTTOM
                canvas.drawRect(
                        -borderSize_finish / 2.0f,
                        getHeight() - borderSize_finish / 2.0f,
                        (this.animationTimingCompletion_border - .75f) * (getWidth() + borderSize_finish) * 2.0f - borderSize_finish / 2.0f,
                        getHeight() + borderSize_finish / 2.0f, paint_border_Bottom);
                canvas.drawRect(
                        (getWidth() + borderSize_finish / 2.0f) - ((this.animationTimingCompletion_border - .75f) * (getWidth() + borderSize_finish) * 2.0f),
                        getHeight() - borderSize_finish / 2.0f,
                        getWidth() + borderSize_finish / 2.0f,
                        getHeight() + borderSize_finish / 2.0f, paint_border_Bottom);
            }
        }else if(isPieceMoving){
            //Perform animation piece move
            if(pieceToMove != null){
                ComposedDrawing drawable = pieceToMove.getAppearances();
                drawable.setBounds(left_pieceToMove,top_pieceToMove,right_pieceToMove,bottom_pieceToMove);
                drawable.draw(canvas);
            }
        }
    }
    @Override
    public boolean isColumnStretchable(int columnIndex) {
        return false;
    }


    /** ======== Init the Board Model ======== **/
    //Init basics UI
    private void setColors() {
        white_color =           GameManager.getAttributeColor(context,R.attr.white_case_color);
        black_color =           GameManager.getAttributeColor(context,R.attr.black_case_color);
        selection_color =       GameManager.getAttributeColor(context,R.attr.selection_case_color);
        eat_color =             GameManager.getAttributeColor(context,R.attr.eat_case_color);
        confirmation_color =    GameManager.getAttributeColor(context,R.attr.confirmation_case_color);
        menaced_color =         GameManager.getAttributeColor(context,R.attr.menaced_case_color);
        rock_color =            GameManager.getAttributeColor(context,R.attr.rock_case_color);
    }
    //Get the dimensions from the Board view in XML file
    private void setDimensions() {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.Board);
        nb_col = a.getInt(R.styleable.Board_nb_column, 0);
        nb_row = a.getInt(R.styleable.Board_nb_row, 0);
        a.recycle();
    }
    //Set the appearances for the board
    private void setAppearances(){
        if(!appearance_possiblepos.isInstancied())      appearance_possiblepos.addLayer(ContextCompat.getDrawable(context, R.drawable.board_simple_shape), selection_color);
        if(!appearance_possiblepos_eat.isInstancied())  appearance_possiblepos_eat.addLayer(ContextCompat.getDrawable(context, R.drawable.board_simple_shape),eat_color);
        if(!appearance_confirmation.isInstancied())     appearance_confirmation.addLayer(ContextCompat.getDrawable(context, R.drawable.board_simple_shape),confirmation_color);
        if(!appearance_menaced.isInstancied())          appearance_menaced.addLayer(ContextCompat.getDrawable(context, R.drawable.board_simple_shape),menaced_color);
        if(!appearance_rock.isInstancied())             appearance_rock.addLayer(ContextCompat.getDrawable(context, R.drawable.board_simple_shape),rock_color);
    }
    //Set the finish screen only
    private void setPaintFinishScreen(){
        paint_background = new Paint();
        paint_border_Bottom = new Paint();
        paint_border_Top = new Paint();
        paint_border_LeftRight = new Paint();

        paint_text_finish_start = new Paint();
        paint_text_finish_players = new Paint();
        paint_text_finish_end = new Paint();

        paint_text_finish_start.setTextAlign(Paint.Align.CENTER);
        paint_text_finish_start.setTextSize(textSizeFinish_start);
        paint_text_finish_players.setTextAlign(Paint.Align.CENTER);
        paint_text_finish_players.setTextSize(textSizeFinish_players);
        paint_text_finish_end.setTextAlign(Paint.Align.CENTER);
        paint_text_finish_end.setTextSize(textSizeFinish_end);

        int color_top = GameManager.getAttributeColor(context,R.attr.colorBorderTopFinishScreen);
        int color_bottom = GameManager.getAttributeColor(context,R.attr.colorBorderBottomFinishScreen);

        int color_text = GameManager.getAttributeColor(context,R.attr.colorTextFinishScreen);
        int color_text_top_bottom = GameManager.getAttributeColor(context,R.attr.colorTextTopBottomFinishScreen);

        paint_background.setColor(GameManager.getAttributeColor(context,R.attr.colorBGFinishScreen));
        //paint_background.setColor(Color.argb(200, 0, 0,0));
        //paint_background.setColor(ContextCompat.getColor(context, R.color.blackTransparent));

        paint_border_Top.setColor(color_top);
        paint_border_Bottom.setColor(color_bottom);
        paint_border_LeftRight.setShader(new LinearGradient(0, 0, 0, getHeight(), new int[] {color_top, color_bottom},null, Shader.TileMode.CLAMP));

        paint_text_finish_start.setColor(color_text_top_bottom);
        paint_text_finish_end.setColor(color_text_top_bottom);
        paint_text_finish_players.setColor(color_text);

        animationTimingCompletion_background = 0.0f;
        animationTimingCompletion_border = 0.0f;

        text_y_start = getHeight()/4 - (int)((paint_text_finish_start.descent() + paint_text_finish_start.ascent()) / 2);
        text_y_mes = getHeight()/2 - (int)((paint_text_finish_players.descent() + paint_text_finish_players.ascent()) / 2);
        text_y_end = 3*getHeight()/4 - (int)((paint_text_finish_end.descent() + paint_text_finish_end.ascent()) / 2);
    }
    //Set the graphic elements for the global board (row and col indicators)
    private void setPaintIndicatorText(){
        text_indicator = new Paint();
        text_indicator.setColor(GameManager.getAttributeColor(context,R.attr.colorTextIndicatorBoard));
        text_indicator.setTextSize(getResources().getDimension(R.dimen.text_size_board_indicator));
        text_indicator.setTextAlign(Paint.Align.CENTER);

        horizontalBarIndicator = new ArrayList<>();
        for (int i = 0; i < nb_col; i++) {
            horizontalBarIndicator.add("" + (char)(i+65));
        }
        verticalBarIndicator = new ArrayList<>();
        for (int i = nb_row; i > 0; i--) {
            verticalBarIndicator.add("" + i);
        }

        float offset_max = getResources().getDimension(R.dimen.board_margin_max);
        float offset_min = getResources().getDimension(R.dimen.board_margin_min);

        float x_size = getWidth() - (offset_max+offset_min);
        float y_size = getHeight() - (offset_max+offset_min);

        horizontal_bar_offset_horizontal = offset_max + (x_size/(nb_col*2.0f));
        horizontal_bar_addition = x_size/nb_col;
        vertical_bar_offset_vertical = offset_min + y_size/(nb_row*2.0f);

        vertical_bar_addition = y_size/nb_row;

        horizontal_bar_offset_vertical = y_size + offset_min + offset_max/2;
        vertical_bar_offset_horizontal = offset_max/2;

        //Font treatment
        Typeface customTypeface = ResourcesCompat.getFont(context, R.font.permanent_marker);
        text_indicator.setTypeface(customTypeface);
    }
    //Method called for initializing the board cases
    public void initBoard(){
        //Create all the views in our model
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
    //Init the game (players and pieces), need to be called by the manager
    public List<Player> initGame_players(){
        System.out.println("GAME MANAGER INIT");
        List<Player> players = new ArrayList<>();

        Player p1 = new Player("JOJO", Color.WHITE);
        Player p2 = new Player("JORDAN",Color.BLACK);
        players.add(p1);
        players.add(p2);
        Piece p1_pawn1 = new Pawn(p1,this.context, Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p1_pawn2 = new Pawn(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p1_pawn3 = new Pawn(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p1_pawn4 = new Pawn(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p1_pawn5 = new Pawn(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p1_pawn6 = new Pawn(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p1_pawn7 = new Pawn(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p1_pawn8 = new Pawn(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK, Piece.DIRECTION.UP);
        Piece p1_tower1 = new Tower(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK);
        Piece p1_knight1 = new Knight(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK);
        Piece p1_bishop1 = new Bishop(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK,Color.BLACK);
        King p1_king = new King(p1,this.context,  Color.WHITE,Color.WHITE,Color.BLACK,Color.BLACK);
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

        p1.addRockPieces(p1_king,new Association_rock(p1_tower1,new Position(0,7),new Position(1,7), new Position(2,7)));
        p1.addRockPieces(p1_king,new Association_rock(p1_tower2,new Position(7,7),new Position(6,7), new Position(5,7)));

        Piece p2_pawn1 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p2_pawn2 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p2_pawn3 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p2_pawn4 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p2_pawn5 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p2_pawn6 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p2_pawn7 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p2_pawn8 = new Pawn(p2,this.context,  Color.BLACK,Color.WHITE,Color.BLACK, Piece.DIRECTION.DOWN);
        Piece p2_tower1 = new Tower(p2,this.context,Color.BLACK,Color.WHITE,Color.BLACK);
        Piece p2_knight1 = new Knight(p2,this.context,Color.BLACK,Color.WHITE,Color.BLACK);
        Piece p2_bishop1 = new Bishop(p2,this.context,Color.BLACK,Color.WHITE,Color.BLACK,Color.WHITE);
        King p2_king = new King(p2,this.context, Color.BLACK,Color.WHITE,Color.BLACK,Color.BLACK);
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

        p2.addRockPieces(p2_king,new Association_rock(p2_tower1,new Position(0,0),new Position(1,0), new Position(2,0)));
        p2.addRockPieces(p2_king,new Association_rock(p2_tower2,new Position(7,0),new Position(6,0), new Position(5,0)));

        //Put all the pieces in the board
        setAPieces(0,1,p2_pawn1);
        setAPieces(1,1,p2_pawn2);
        setAPieces(2,1,p2_pawn3);
        setAPieces(3,1,p2_pawn4);
        setAPieces(4,1,p2_pawn5);
        setAPieces(5,1,p2_pawn6);
        setAPieces(6,1,p2_pawn7);
        setAPieces(7,1,p2_pawn8);

        setAPieces(0,0,p2_tower1);
        setAPieces(1,0,p2_knight1);
        setAPieces(2,0,p2_bishop1);
        setAPieces(3,0,p2_queen);
        setAPieces(4,0,p2_king);
        setAPieces(6,0,p2_knight2);
        setAPieces(5,0,p2_bishop2);
        setAPieces(7,0,p2_tower2);

        setAPieces(0,6,p1_pawn1);
        setAPieces(1,6,p1_pawn2);
        setAPieces(2,6,p1_pawn3);
        setAPieces(3,6,p1_pawn4);
        setAPieces(4,6,p1_pawn5);
        setAPieces(5,6,p1_pawn6);
        setAPieces(6,6,p1_pawn7);
        setAPieces(7,6,p1_pawn8);

        setAPieces(0,7,p1_tower1);
        setAPieces(1,7,p1_knight1);
        setAPieces(2,7,p1_bishop1);
        setAPieces(3,7,p1_queen);
        setAPieces(4,7,p1_king);
        setAPieces(6,7,p1_knight2);
        setAPieces(5,7,p1_bishop2);
        setAPieces(7,7,p1_tower2);
        //Commit changes for displaying
        commitChanges();
        return players;
    }
    //Init the game (onclick cases event), need to be called by the manager
    public void initGame_onclk(OnClickListener clk){
        for (Case c: cases) {
            c.setOnClickListener(clk);
        }
    }
    //Create a Case View
    private Case createCase(int col, int row, boolean isWhite){
        TableRow.LayoutParams layout = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        Case case_tab = new Case(context,null);
        case_tab.setLayoutParams(layout);
        case_tab.setColumn(col);
        case_tab.setRow(row);
        case_tab.setWhite(isWhite);
        case_tab.setColor_white(white_color);
        case_tab.setColor_black(black_color);
        case_tab.setEndCase(row == 0 || row == (nb_row-1));
        return case_tab;
    }


    /** ======== Manip the Board Model ======== **/
    //Say if a position for a move on the board is correct or not
    public boolean isGoodPos(int x, int y){
        return ((x>=0 && x<nb_col) && (y>=0 && y<nb_row));
    }
    //Get a Case element with the position on the board, null instead
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
    //Clear the board states
    public void clear(){
        for (Case c: cases) {
            c.clear();
        }
    }
    //Set the end of the board
    public void onEndOfGame(String start, String players, String end){
        this.finishMessage_start = start;
        this.finishMessage_players = players;
        this.finishMessage_end = end;

        isFinishScreen = true;

        //If we have the animation on finish screen
        if(GameManager.ANIMATION_FINISH) {
            ValueAnimator animator_background = ValueAnimator.ofFloat(0.0f, 1.0f);
            animator_background.setDuration(this.border_globalDuration);
            animator_background.addUpdateListener(animation -> {
                animationTimingCompletion_background = (Float) animation.getAnimatedValue();
                invalidate();
            });
            animator_background.setInterpolator(new BounceInterpolator());

            ValueAnimator animator_border = ValueAnimator.ofFloat(0.0f, 1.0f);
            animator_border.setDuration(this.border_globalDuration - this.text_offsetTime);
            animator_border.addUpdateListener(animation -> {
                animationTimingCompletion_border = (Float) animation.getAnimatedValue();
                invalidate();
            });

            animator_border.setStartDelay(text_offsetTime);
            animator_border.setInterpolator(new LinearInterpolator());

            animator_background.start();
            animator_border.start();
        }else{
            //Else we just dra with the finish values, and not a ValueAnimator
            animationTimingCompletion_background = 1.0f;
            animationTimingCompletion_border = 1.0f;
            invalidate();
        }
    }
    //Set the UI for choice in game board changing
    public void onChangePieceShape(Player curP,Function<Piece,Void> fnc_oclk_choices){
        //First set the screen to be "visible"
        this.onScreenView.setVisibility(VISIBLE);

        //Next compute onclick for the game changing piece
        this.onScreenView.setOclkPerformedFunctionPieces(fnc_oclk_choices);

        //Then MAJ all components for UI interfaces
        this.onScreenView.clearChoices();
        int color_primary = curP.getColor();
        int color_secondary = (color_primary == Color.BLACK)? Color.WHITE : Color.BLACK;
        int color_base = Color.WHITE;
        int color_border = Color.BLACK;

        this.onScreenView.addElementForChoice(new Tower(curP,this.context,  color_primary,color_base,color_border));
        this.onScreenView.addElementForChoice(new Queen(curP,this.context,  color_primary,color_base,color_border));
        this.onScreenView.addElementForChoice(new Knight(curP,this.context,  color_primary,color_base,color_border));
        this.onScreenView.addElementForChoice(new Bishop(curP,this.context,  color_primary,color_base,color_border,color_secondary));

        //Else commit the changes (clear and recreate UIs)
        this.onScreenView.commitChanges();
    }
    //Hide the current screen View for chose a Piece for transformation
    public void restart_no_screen_view(){
        this.onScreenView.setVisibility(INVISIBLE);
    }
    //Say if there is a piece between two positions
    public boolean noPiecesBetween(Position p1, Position p2) {
        if(DEBUG_FOR_BOARD_LOGIC) {
            System.out.println("NO PIECES BETWEEN : ");
            System.out.println("START : " + p1);
            System.out.println("END : " + p2);
        }


        Position incr = p2.difference(p1);
        int coefX = (incr.getX()<0)? -1 : 1;
        int coefY = (incr.getY()<0)? -1 : 1;

        if(DEBUG_FOR_BOARD_LOGIC) System.out.println("INCR : " + incr);

        int nbX = coefX * incr.getX();
        int nbY = coefY * incr.getY();

        if(DEBUG_FOR_BOARD_LOGIC) {
            System.out.println("nbX : " + nbX);
            System.out.println("nbY : " + nbY);
        }

        for (int i = 0; i <= nbY; i++) {
            for (int j = 0; j <= nbX; j++) {
                if((i!=0 || j!=0) && (i!=nbY || j!=nbX)) {
                    Position posToWatch = new Position(p1.getX() + j * coefX, p1.getY() + i * coefY);
                    if(DEBUG_FOR_BOARD_LOGIC) System.out.println(" => POSTOWATCH : " + posToWatch);
                    Case curCaseToWatch = cases[posToWatch.getY() * nb_col + posToWatch.getX()];

                    if (curCaseToWatch.getPiece() != null) {
                        if(DEBUG_FOR_BOARD_LOGIC) System.out.println(" PIECE TO CANCEL : " +curCaseToWatch.getPiece());
                        return false;
                    }
                }else{
                    if(DEBUG_FOR_BOARD_LOGIC) {
                        System.out.println(" => POST NOT WATCH incr i : " +i*coefY);
                        System.out.println(" => POST NOT WATCH incr j : " +j*coefX);
                    }

                }
            }
        }
        return true;
    }


    /** ======== For layout rendering ======== **/
    //Init the board XML appearance
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
    //Maj the size of the Cases
    public void majSizeCases(int taille_case){
        for (Case c: cases) {
            c.setDessiredDimention(taille_case);
        }
    }
    //Set the screen view for the Pieces transformations
    public void setOnScreenView(ChangePieceScreen onScreenView) {
        this.onScreenView = onScreenView;
    }


    /** ======== Methods for performing changes on the board and MAJ Model and Layout ======== **/
    //Method called when you want to apply changes
    public void commitChanges(){
        //System.out.println("COMMIT CHANGES ON BOARD");
        for (Case c: changedCases) {
            c.commit_changes();
        }
        changedCases.clear();
    }

    /**Basic methods for create changes on the board**/
    //Set a piece to the param Position
    //Assuming this position to be correct
    public void setAPieces(int col, int row, @Nullable Piece p){
        Case caseInCase = cases[col+row*nb_col];
        if(!changedCases.contains(caseInCase)){
            changedCases.add(caseInCase);
        }
        caseInCase.setPiece(p);
    }
    //Set a possible position (true or false)
    //Assuming this position to be correct
    public void setPossiblePos(int col, int row, boolean pos){
       Case caseInCase = cases[col+row*nb_col];
        if(!changedCases.contains(caseInCase)){
            changedCases.add(caseInCase);
        }
        caseInCase.setPossible_pos(pos);
    }
    //Set a possible position ROCK (Association_rock)
    //Assuming this position to be correct
    public void setPossiblePosRock(int col, int row, Association_rock as){
        Case caseInCase = cases[col+row*nb_col];
        if(!changedCases.contains(caseInCase)){
            changedCases.add(caseInCase);
        }
        caseInCase.set_rock_elem(as);
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
    //Set a possible dangerous position (true or false)
    //Assuming this position to be correct
    public void setPossibleCaseWithMenaceOnIt(int col, int row, boolean men){
        //Log.i(log_tag,"BOARD : SET A PRESELECTED POS ON : (" +col+","+row+") : " + pos);
        Case caseInCase = cases[col+row*nb_col];
        if(!changedCases.contains(caseInCase)){
            changedCases.add(caseInCase);
        }
        caseInCase.setWith_menace_on_it(men);
    }


    /**ANIMATION TREATMENT**/
    //Set a list of pieces (on startElem) with a continuous displacement on the board (to endElem)
    public void animatedDisplacement(List<Case> startElem, List<Case> endElem, int nbELem, AnimatorListenerAdapter onTheEnd){
        for (int i = 0; i < nbELem; i++) {
            Case start = startElem.get(i);
            Case end = endElem.get(i);

            //In this func, we will only perform animation visual effect on the board, the gameplay mecanism is performed by onTheEnd
            Piece p_dep = start.getPiece();

            if (DEBUG_FOR_BOARD_ANIMATION) {
                System.out.println("ANIMATED DISPLACEMENT : " + p_dep);
                System.out.println("CASE START : " + start);
                System.out.println("CASE END : " + end);
            }

            if (p_dep != null) {
                //Now we can perform the continuous movement of the piece
                isPieceMoving = true;
                pieceToMove = p_dep;

                //Get the current size of the piece
                //Need to consider the padding with the current case
                int dimension = (int) GameManager.convertDpToPixel(getResources().getDimension(R.dimen.case_pieces_padding), context);
                int w_piece = start.getWidth() - 2 * dimension;
                int h_piece = start.getHeight() - 2 * dimension;


                //Get the position of the two cases depending on their positions in the current Board
                Rect ovb_start = new Rect();
                start.getDrawingRect(ovb_start);
                this.offsetDescendantRectToMyCoords(start, ovb_start);
                int start_y = ovb_start.top + dimension;
                int start_x = ovb_start.left + dimension;
                Rect ovb_end = new Rect();
                end.getDrawingRect(ovb_end);
                this.offsetDescendantRectToMyCoords(end, ovb_end);
                int end_y = ovb_end.top + dimension;
                int end_x = ovb_end.left + dimension;

                if (DEBUG_FOR_BOARD_ANIMATION) {
                    System.out.println("=>WIDTH P : " + w_piece);
                    System.out.println("=>HEIGHT P : " + h_piece);
                    System.out.println("=>POS START : " + start_x + "," + start_y);
                    System.out.println("=>POS END : " + end_x + "," + end_y);
                }

                //Now we can perform all the animation
                ValueAnimator anim_piece = ValueAnimator.ofFloat(0.0f, 1.0f);
                anim_piece.setDuration(this.piece_globalDuration);
                anim_piece.addUpdateListener(animation -> {
                    float value = (Float) animation.getAnimatedValue();
                    top_pieceToMove = (int) (start_y + (end_y - start_y) * value);
                    left_pieceToMove = (int) (start_x + (end_x - start_x) * value);
                    bottom_pieceToMove = top_pieceToMove + h_piece;
                    right_pieceToMove = left_pieceToMove + w_piece;
                    invalidate();
                });
                anim_piece.addListener(onTheEnd);

                anim_piece.setInterpolator(new AccelerateInterpolator());
                anim_piece.start();
            }
        }
    }
    //Restart the animation
    public void restart_no_animation_context(){
        isPieceMoving = false;
        pieceToMove = null;
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
                '}';
    }
}
