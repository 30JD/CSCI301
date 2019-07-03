package cs301.cs.wm.edu.jundaan.falstad;

import android.graphics.drawable.Drawable;
import android.view.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import cs301.cs.wm.edu.jundaan.R;

import static android.graphics.Color.parseColor;
import static android.util.Log.v;

/**
 * Add functionality for double buffering to an AWT Panel class.
 * Used for drawing a maze.
 * 
 * @author Peter Kemper
 *
 */
public class MazePanel extends View {
	/* Panel operates a double buffer see
	 * http://www.codeproject.com/Articles/2136/Double-buffer-in-standard-Java-AWT
	 * for details
	 */
	// bufferImage can only be initialized if the container is displayable,
	// uses a delayed initialization and relies on client class to call initBufferImage()
	// before first use
	//private Image bufferImage;
	//private Graphics2D graphics; // obtained from bufferImage,
	// graphics is stored to allow clients to draw on the same graphics object repeatedly
	// has benefits if color settings should be remembered for subsequent drawing operations
	//private Color color;

	private String text;
	private int color;
	private int size;
    public int state = 0;


    private int point1x;
    private int point1y;
    private int point2x;
    private int point2y;


    private Paint painter;
    private Canvas canvas;
    private Bitmap bitmap;
    private Drawable mazeBackground;
    private int counter = 0;
    private List<int[]> toDraw = new ArrayList<int[]>();
    private List<int[]> toDrawBackUp = new ArrayList<int[]>();
    private List<int[]> LinesNorth = new ArrayList<int[]>();
    private List<int[]> LinesWest = new ArrayList<int[]>();
    private List<int[]> currentPosition = new ArrayList<int[]>();
    private List<int[]> solutionLines = new ArrayList<int[]>();
	
	/**
	 * Constructor. Object is not focusable.
	 */
	public MazePanel(Context context, AttributeSet attributeSet) {
	    super(context, attributeSet);
	    bitmap = Bitmap.createBitmap(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
	    canvas = new Canvas(bitmap);
	    painter = new Paint();
		//setFocusable(false);
		//bufferImage = null; // bufferImage initialized separately and later
		//graphics = null;	// same for graphics
		
		
	}

	public MazePanel(Context context) {
		super(context);
		bitmap = Bitmap.createBitmap(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		painter = new Paint();
		//setFocusable(false);
		//bufferImage = null; // bufferImage initialized separately and later
		//graphics = null;	// same for graphics


	}

	protected void onDraw(Canvas canvas){
		Log.v("ondraw", String.valueOf(counter));
		super.onDraw(canvas);
		canvas.drawBitmap(bitmap, 0,0, painter);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.setMeasuredDimension(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
	}

	public void update(){
		invalidate();
	}

	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints){
		painter.setStyle(Paint.Style.FILL);
		Path path = new Path();
		path.reset();
		path.moveTo(xPoints[0], yPoints[0]);
		for(int i = 1; i < nPoints; i++){
			path.lineTo(xPoints[i], yPoints[i]);
		}
		path.lineTo(xPoints[0], yPoints[0]);
		canvas.drawPath(path, painter);
	}

	public void fillRect(int x, int y, int width, int height){
		painter.setStyle(Paint.Style.FILL);
		Rect rect = new Rect(x, y, x + width, y + height);
		canvas.drawRect(rect, painter);
	}

	public void drawLine(int x1, int y1, int x2, int y2){
		canvas.drawLine(x1, y1, x2, y2, painter);
	}

	public void fillOval(int x, int y, int width, int height){
		painter.setStyle(Paint.Style.FILL);
		RectF oval = new RectF(x, y, x + width, y + height);
		canvas.drawOval(oval, painter);
	}

	public void setSegColor(int[] color){
		painter.setColor(Color.rgb(color[0], color[1], color[2]));
	}

	/*
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	*/
	/**
	 * Method to draw the buffer image on a graphics object that is
	 * obtained from the superclass. 
	 * Warning: do not override getGraphics() or drawing might fail. 
	 */
	/*
	public void update() {
		paint(getGraphics());
	}
	
	/**
	 * Draws the buffer image to the given graphics object.
	 * This method is called when this panel should redraw itself.
	 * The given graphics object is the one that actually shows 
	 * on the screen.
	 */

    /*
	public void paint(Graphics g) {
		if (null == g) {
			System.out.println("MazePanel.paint: no graphics object, skipping drawImage operation");
		}
		else {
			g.drawImage(bufferImage,0,0,null);	
		}
	}
	
	public void drawLine(int startX, int startY, int stopX, int stopY) {
		Graphics g = this.getBufferGraphics();
		g.drawLine(startX, startY, stopX, stopY);
	}

	/**
	 * Obtains a graphics object that can be used for drawing.
	 * This MazePanel object internally stores the graphics object 
	 * and will return the same graphics object over multiple method calls. 
	 * The graphics object acts like a notepad where all clients draw 
	 * on to store their contribution to the overall image that is to be
	 * delivered later.
	 * To make the drawing visible on screen, one needs to trigger 
	 * a call of the paint method, which happens 
	 * when calling the update method. 
	 * @return graphics object to draw on, null if impossible to obtain image
	 */

    /*
	public Graphics getBufferGraphics() {
		// if necessary instantiate and store a graphics object for later use
		if (null == graphics) { 
			if (null == bufferImage) {
				bufferImage = createImage(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
				if (null == bufferImage)
				{
					System.out.println("Error: creation of buffered image failed, presumedly container not displayable");
					return null; // still no buffer image, give up
				}		
			}
			graphics = (Graphics2D) bufferImage.getGraphics();
			if (null == graphics) {
				System.out.println("Error: creation of graphics for buffered image failed, presumedly container not displayable");
			}
			else {
				// System.out.println("MazePanel: Using Rendering Hint");
				// For drawing in FirstPersonDrawer, setting rendering hint
				// became necessary when lines of polygons 
				// that were not horizontal or vertical looked ragged
				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			}
		}
		return graphics;
	}
	*/

	public void setColor(String color) {
		switch (color) {
			case "blue":
				painter.setColor(Color.BLUE);
				Log.v("jd", "blue");

				break;
			case "darkGray":
				painter.setColor(Color.DKGRAY);
                Log.v("jd", "gray");

				break;
			case "black":
				painter.setColor(Color.BLACK);
                Log.v("jd", "black");

				break;
			case "white":
				painter.setColor(Color.WHITE);
                Log.v("jd", "white");

				break;
			case "red":
				painter.setColor(Color.RED);
				break;
			case "gray":
				painter.setColor(Color.GRAY);
				break;
			case "yellow":
				painter.setColor(Color.YELLOW);
				break;
			case "green":
				painter.setColor(Color.GREEN);
				break;
		}
	}

	public static int getRGB(int[] col){
	    return Color.rgb(col[0], col[1], col[2]);
    }
}
