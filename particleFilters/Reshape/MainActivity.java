package com.example.app3_particles;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Smart Phone Sensing Example 6. Object movement and interaction on canvas.
 */
public class MainActivity extends Activity implements OnClickListener {

    /**
     * The buttons.
     */
    private Button up, left, right, down;
    /**
     * The text view.
     */
    private TextView textView;
    /**
     * The shape.
     */
    private ArrayList<ArrayList<ShapeDrawable>> drawable, drawablebefore;
    /**
     * The canvas.
     */
    private Canvas canvas;

    /**
     * probability
     */
    private ArrayList<ArrayList<Integer>> probability ;

    private int resampling, maxprob;
    private float convergence;
    /**
     * The walls.
     */
    private List<ShapeDrawable> walls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the buttons
        up = (Button) findViewById(R.id.button1);
        left = (Button) findViewById(R.id.button2);
        right = (Button) findViewById(R.id.button3);
        down = (Button) findViewById(R.id.button4);

        // set the text view
        textView = (TextView) findViewById(R.id.textView1);

        // set listeners
        up.setOnClickListener(this);
        down.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);

        // get the screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        int ch = (int)  (height * 0.276) + 1;
        int cw = (int)  (width * 0.276) + 1;
        int roomwidth = (int) ((width - cw) * 0.42666); // 460
        int roomheight = (int) ((height - ch)  * 0.05555);  // 77
        ch-=20;
        cw/=2;


        // create a drawable object
        drawable = new ArrayList<ArrayList<ShapeDrawable>>();
        drawablebefore = new ArrayList<ArrayList<ShapeDrawable>>();
        probability = new ArrayList<ArrayList<Integer>>();
        resampling = 0;
        maxprob = 1;
        convergence = (float) 0.1;

        for (int fy = 40; fy < roomheight * 18 ; fy += 30 ){
  //      for (int fy = 600; fy < roomheight * 16 + 20; fy += 20 ){
            ArrayList<ShapeDrawable> dotx = new ArrayList<>();
            ArrayList<ShapeDrawable> dotxb = new ArrayList<>();
            ArrayList<Integer> probx = new ArrayList<>();
//            for (int fx = roomwidth/2; fx < width - roomwidth/2; fx += 20){

            for (int fx = cw; fx < width-cw; fx += 30){
                ShapeDrawable dot = new ShapeDrawable(new OvalShape());
                ShapeDrawable dotb = new ShapeDrawable(new OvalShape());
                dot.getPaint().setColor(Color.BLUE);
                dot.setBounds(fx-4, fy-4, fx+4, fy+4);
                dotx.add(dot);
                dotb.getPaint().setColor(Color.BLUE);
                dotb.setBounds(fx-4, fy-4, fx+4, fy+4);
                dotxb.add(dotb);
                probx.add(1);
            }
            probability.add(probx);
            drawable.add(dotx);
            drawablebefore.add(dotxb);
        }
        walls = new ArrayList<>();
        //bottom rooms
        for (int i = 0; i < 17; i++){
            ShapeDrawable wall_bottom = new ShapeDrawable(new RectShape());
            if (i >= 15 || i == 11 || i == 10){
                if (i == 16){
                    ShapeDrawable wall_bottom1 = new ShapeDrawable(new RectShape());
                    ShapeDrawable wall_bottom2 = new ShapeDrawable(new RectShape());
                    ShapeDrawable wall_bottom3 = new ShapeDrawable(new RectShape());
                    wall_bottom1.setBounds(cw+roomwidth/2, height - i*roomheight - ch, cw+roomwidth, height - ch - 50 - (i-1)* roomheight);
                    walls.add(wall_bottom1);
                    wall_bottom2.setBounds(cw, 20, cw+roomwidth, height - ch - i* roomheight);
                    walls.add(wall_bottom2);
                    wall_bottom3.setBounds(cw, 0-20, width-cw, 20);
                    walls.add(wall_bottom3);

                }

                wall_bottom.setBounds(cw,height - i* roomheight - ch - 10, cw+roomwidth, height - ch - i* roomheight);
            }else {
                wall_bottom.setBounds(cw,height - i* roomheight - 510 - 5, cw+roomwidth, height - ch - i* roomheight);
            }

            if (i < 12 || i > 14 ) {
                walls.add(wall_bottom);
            }


        }
        // top rooms
        for (int i = 0; i <= 16; i++){
            ShapeDrawable wall_top = new ShapeDrawable(new RectShape());
            if (i >= 14){
                wall_top.setBounds(width-cw - roomwidth, i* roomheight + 20 - 10, width-cw, i* roomheight + 20 );
            }else{
                wall_top.setBounds(width-cw - roomwidth, i* roomheight + 20 - 5, width-cw, i* roomheight + 20 );
            }
            if (i == 16){
                ShapeDrawable wall_top1 = new ShapeDrawable(new RectShape());
                wall_top1.setBounds(cw, height-520, width-cw, height-490 );
                walls.add(wall_top1);
                wall_top.setBounds(width-cw - roomwidth, (i-1)* roomheight + 20, width-cw, height-510 );
            }

            walls.add(wall_top);
        }
        ShapeDrawable wallside = new ShapeDrawable(new RectShape());
        wallside.setBounds(cw-10,0,cw+5,height - 490);
        ShapeDrawable wallside2 = new ShapeDrawable(new RectShape());
        wallside2.setBounds(width-cw-5,0,width-cw+10,  height - 490);
        walls.add(wallside);
        walls.add(wallside2);



        // create a canvas
        ImageView canvasView = (ImageView) findViewById(R.id.canvas);
        Bitmap blankBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        canvasView.setImageBitmap(blankBitmap);

        // draw the objects

        for (int fy = 0; fy < drawable.size(); fy ++){
            for(ShapeDrawable dots : drawable.get(fy))
                dots.draw(canvas);
        }

        for(ShapeDrawable wall : walls)
            wall.draw(canvas);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        // This happens when you click any of the four buttons.
        // For each of the buttons, when it is clicked we change:
        // - The text in the center of the buttons
        // - The margins
        // - The text that shows the margin
        resampling++;
        int noise = new Random().nextInt(10)-5;
        int noise2 = new Random().nextInt(10)-5;

        switch (v.getId()) {
            // UP BUTTON
            case R.id.button1: {
                for(int fy=0; fy < drawable.size(); fy++){
                    for (int fx=0; fx < drawable.get(fy).size(); fx++){
                        Rect r =  new Rect(drawable.get(fy).get(fx).getBounds());
                        drawable.get(fy).get(fx).setBounds(r.left+noise2,r.top-20+noise,r.right+noise2,r.bottom-20+noise);
                        noise2 = new Random().nextInt(10)-5;
                        noise = new Random().nextInt(10)-5;
                        drawablebefore.get(fy).get(fx).setBounds(r.left,r.top,r.right,r.bottom);
                        textView.setText("\n\tMove Up" + "\n\tTop Margin = "
                                + drawable.get(fy).get(fx).getBounds().top);
                    }
                }

                break;
            }
            // DOWN BUTTON
            case R.id.button4: {
                for(int fy=0; fy < drawable.size(); fy++){
                    for (int fx=0; fx < drawable.get(fy).size(); fx++){
                        Rect r =  new Rect(drawable.get(fy).get(fx).getBounds());
                        drawable.get(fy).get(fx).setBounds(r.left+noise2,r.top+20+noise,r.right+noise2,r.bottom+20+noise);
                        noise2 = new Random().nextInt(10)-5;
                        noise = new Random().nextInt(10)-5;
                        drawablebefore.get(fy).get(fx).setBounds(r.left,r.top,r.right,r.bottom);
                        textView.setText("\n\tMove Down" + "\n\tTop Margin = "
                                + drawable.get(fy).get(fx).getBounds().top);
                    }
                }
                break;
            }
            // LEFT BUTTON
            case R.id.button2: {
                for(int fy=0; fy < drawable.size(); fy++){
                    for (int fx=0; fx < drawable.get(fy).size(); fx++){
                        Rect r =  new Rect(drawable.get(fy).get(fx).getBounds());
                        drawable.get(fy).get(fx).setBounds(r.left-20+noise2,r.top+noise,r.right-20+noise2,r.bottom+noise);
                        noise2 = new Random().nextInt(10)-5;
                        noise = new Random().nextInt(10)-5;
                        drawablebefore.get(fy).get(fx).setBounds(r.left,r.top,r.right,r.bottom);
                        textView.setText("\n\tMove Left" + "\n\tTop Margin = "
                                + drawable.get(fy).get(fx).getBounds().top);
                    }
                }

                break;
            }
            // RIGHT BUTTON
            case R.id.button3: {
                for(int fy=0; fy < drawable.size(); fy++){
                    for (int fx=0; fx < drawable.get(fy).size(); fx++){
                        Rect r =  new Rect(drawable.get(fy).get(fx).getBounds());
                        drawable.get(fy).get(fx).setBounds(r.left+20+noise2,r.top+noise,r.right+20+noise2,r.bottom+noise);
                        noise2 = new Random().nextInt(10)-5;
                        noise = new Random().nextInt(10)-5;
                        drawablebefore.get(fy).get(fx).setBounds(r.left,r.top,r.right,r.bottom);
                        textView.setText("\n\tMove Right" + "\n\tTop Margin = "
                                + drawable.get(fy).get(fx).getBounds().top);
                    }
                }
                break;
            }
        }
        // if there is a collision between the dot and any of the walls
            // reposition the dots
        if (isCollision()){
           int countersafe=0;
            for(int fy=0; fy < drawable.size(); fy++) {
                for (int fx = 0; fx < drawable.get(fy).size(); fx++) {
                    for(ShapeDrawable wall : walls) {
                        if (isCollision(wall, drawable.get(fy).get(fx),drawablebefore.get(fy).get(fx))){
                            int fxr = 0;
                            int fyr =0;
                            probability.get(fy).set(fx,0);
                            boolean reshape = true;
                            while(reshape == true){

                                fyr = new Random().nextInt(drawable.size());
                                fxr = new Random().nextInt(drawable.get(0).size());
                                for(ShapeDrawable wall2 : walls) {
                                    if (isCollision(wall2, drawable.get(fyr).get(fxr),drawablebefore.get(fyr).get(fxr))) {
                                        reshape=true;
                                        countersafe++;
                                        break;
                                    }else{
                                        reshape=false;
                                    }
                                }
                                if (countersafe > drawable.size()*drawable.get(fy).size()){
                                    fxr=fx;
                                    fyr=fy;
                                    break;
                                }
                            }
                            Rect r;
                            if (countersafe > drawable.size()*drawable.get(fy).size()){
                                 r = drawablebefore.get(fyr).get(fxr).getBounds();
                            }else{
                                r = drawable.get(fyr).get(fxr).getBounds();
                            }
                            Integer dummy = probability.get(fyr).get(fxr);
                            dummy++;
                            probability.get(fyr).set(fxr, dummy);
                            if (probability.get(fyr).get(fxr) > maxprob)
                                maxprob = probability.get(fyr).get(fxr);
                            drawable.get(fy).get(fx).setBounds(r.left,r.top,r.right,r.bottom);
                           // drawable.get(fy).get(fx).setBounds(width/2-8 , height/2-8, width/2+8, height/2+8);

                        }
                    }
                }
            }
        }



        if (resampling >= 10){
            Toast.makeText(getApplication(), "resampling", Toast.LENGTH_SHORT).show();
            if (convergence <= 0.5){
                convergence+= 0.1;
            }
            int tempmaxprob = maxprob;
            maxprob = 1;
            for (int fy = 0; fy < drawable.size(); fy ++ ){
                int sizex = drawable.get(fy).size();
                for (int fx = 0; fx < drawable.get(fy).size(); fx ++){
                    int fyr = 0;
                    int fxr = 0;
                    Rect r;
                    if (probability.get(fy).get(fx) >= tempmaxprob*convergence){
                        r = drawable.get(fy).get(fx).getBounds();
                        drawable.get(fy).get(fx).setBounds(r.left,r.top,r.right,r.bottom);
                        drawablebefore.get(fy).get(fx).setBounds(r.left,r.top,r.right,r.bottom);
                        if (probability.get(fy).get(fx) > maxprob)
                            maxprob = probability.get(fy).get(fx);

                    }else{
                        do {
                            fyr = new Random().nextInt(drawable.size());
                            fxr = new Random().nextInt(drawable.get(fy).size());
                        }while (probability.get(fyr).get(fxr)  < tempmaxprob*convergence);

                      /*  if (convergence <= 0.4){
                            if (fx > drawable.get(fy).size()) break;
                            probability.get(fy).remove(fx);
                            drawablebefore.get(fy).remove(fx);
                            drawable.get(fy).remove(fx);
                            fx--;
                        }else{
                        */    probability.get(fy).set(fx,0);
                            r = drawable.get(fyr).get(fxr).getBounds();
                            drawable.get(fy).get(fx).setBounds(r.left,r.top,r.right,r.bottom);
                            drawablebefore.get(fy).get(fx).setBounds(r.left,r.top,r.right,r.bottom);
                            Integer dummy = probability.get(fyr).get(fxr);
                            dummy++;
                            probability.get(fyr).set(fxr, dummy);
                            if (probability.get(fyr).get(fxr) > maxprob)
                                maxprob = probability.get(fyr).get(fxr);
                        //}
                    }
                }
            }
            resampling = 0;
        }

        // redrawing of the object
        canvas.drawColor(Color.WHITE);
        for (int fy = 0; fy < drawable.size(); fy ++){
            for(ShapeDrawable dots : drawable.get(fy))
                dots.draw(canvas);
        }
        for(ShapeDrawable wall : walls)
            wall.draw(canvas);
    }

    /**
     * Determines if the drawable dot intersects with any of the walls.
     * @return True if that's true, false otherwise.
     */
    private boolean isCollision() {
        for(ShapeDrawable wall : walls) {
            for(int fy=0; fy < drawable.size(); fy++)
                for (int fx=0; fx < drawable.get(0).size(); fx++)
                    if(isCollision(wall,drawable.get(fy).get(fx),drawablebefore.get(fy).get(fx) ))
                        return true;
                        //return false;

        }
        return false;
    }

    /**
     * Determines if two shapes intersect.
     * @param first The first shape.
     * @param second The second shape.
     * @return True if they intersect, false otherwise.
     */
    private boolean isCollision(ShapeDrawable first, ShapeDrawable second, ShapeDrawable third) {
        Rect secondRect = new Rect(second.getBounds()); // point actual
        Rect thirdRect = new Rect(third.getBounds()); //point before
        Rect stepRect = new Rect();
        if (secondRect.left < thirdRect.left - 6 || secondRect.top < thirdRect.top - 6){
            if (secondRect.left > thirdRect.left){
                stepRect.set(thirdRect.left, secondRect.top, secondRect.right ,thirdRect.bottom);
            }else if (secondRect.top > thirdRect.top){
                stepRect.set(secondRect.left, thirdRect.top, thirdRect.right ,secondRect.bottom);
            }else{
                stepRect.set(secondRect.left, secondRect.top, thirdRect.right ,thirdRect.bottom);
            }
        }else {
            if (secondRect.left < thirdRect.left){
                stepRect.set(secondRect.left, thirdRect.top, thirdRect.right ,secondRect.bottom);
            }else if (secondRect.top < thirdRect.top){
                stepRect.set(thirdRect.left, secondRect.top, secondRect.right ,thirdRect.bottom);
            }else{
                stepRect.set(thirdRect.left, thirdRect.top, secondRect.right ,secondRect.bottom);
            }
        }
        return stepRect.intersect(first.getBounds());
    }
}

