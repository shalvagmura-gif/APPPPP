package com.citydrive.twenty;
import android.app.Activity;
import android.os.Bundle;
import android.content.*;
import android.graphics.*;
import android.view.*;
public class MainActivity extends Activity{
 public void onCreate(Bundle b){super.onCreate(b);getWindow().getDecorView().setSystemUiVisibility(5894|1024|512);setContentView(new Game(this));}
 class Game extends View{
 Canvas c;Paint p=new Paint(3);SharedPreferences pref;long last=System.nanoTime();
 String[] cars={"Toyota Corolla","Kia Niro","Hyundai Elantra","Toyota Camry","Kia Sportage","Hyundai Tucson","Honda Civic","Mazda 3","Skoda Octavia","Toyota RAV4","Ford Mustang","Chevrolet Camaro","Nissan Altima","Subaru Outback","Honda CR-V","Toyota Highlander","Jeep Wrangler","Hummer H2","Ford F-150","Ford Super Duty","Toyota Hilux","Toyota Land Cruiser","Chevrolet Silverado","BMW 7 Series"};
 int[] price={0,800,1400,2100,2800,3500,4100,4700,5400,6500,7500,8500,9500,10500,11600,13000,14500,16000,18000,21000,23000,27000,29000,36000};
 int[] cols={0xffdce1e5,0xff4daeb5,0xffe7b889,0xffa8b5d7,0xff9dd28c,0xffe9d386,0xffd55b5b,0xffa1aec0,0xffe6e1d1,0xffbdc99d,0xffffb42d,0xffb74f55,0xff85b2ba,0xffa3bdad,0xffe5e2df,0xff9b9da7,0xff81ad85,0xffbfb3a7,0xffb76562,0xff647b9e,0xffe0d2ad,0xffa0abb6,0xffb5c0a9,0xff484e61};
 String[] modes={"FREE DRIVE","STREET RACE","TAXI CAREER","DRIVING SCHOOL","MOUNTAIN 4X4"};
 int screen=1,mode=0,car=0,owned=1,money=1600,gear=0,level=1,lesson=0,trip=0,stage=0,trafficFines=0;
 float speed=0,steer=0,lane=0,distance=0,progress=0,target=700,opponent=0,signal=0,toastTime=5;
 boolean gas=false,brake=false,left=false,right=false,hand=false,awd=false,mission=false;
 String toast="WELCOME TO CITY DRIVE 20";
 float sx,sy;
 Game(Context x){super(x);setLayerType(View.LAYER_TYPE_SOFTWARE,null);pref=x.getSharedPreferences("save",0);car=pref.getInt("car",0);money=pref.getInt("money",1600);owned=pref.getInt("owned",1);level=pref.getInt("level",1);lesson=pref.getInt("lesson",0);}
 void store(){pref.edit().putInt("car",car).putInt("money",money).putInt("owned",owned).putInt("level",level).putInt("lesson",lesson).apply();}
 void say(String s){toast=s;toastTime=3;}
 void rect(float x,float y,float w,float h,int color){p.setColor(color);p.setStyle(Paint.Style.FILL);c.drawRect(x,y,x+w,y+h,p);}
 void round(float x,float y,float w,float h,int color){p.setColor(color);c.drawRoundRect(x,y,x+w,y+h,12,12,p);}
 void circ(float x,float y,float rad,int color){p.setColor(color);c.drawCircle(x,y,rad,p);}
 void line(float x,float y,float xx,float yy,int color,float sw){p.setColor(color);p.setStrokeWidth(sw);c.drawLine(x,y,xx,yy,p);}
 void txt(String s,float x,float y,int size,int color){p.setColor(color);p.setTextSize(size);p.setTypeface(Typeface.create("sans-serif-condensed",Typeface.BOLD));p.setTextAlign(Paint.Align.CENTER);c.drawText(s,x,y,p);}
 void poly(int color,float... z){Path path=new Path();path.moveTo(z[0],z[1]);for(int i=2;i<z.length;i+=2)path.lineTo(z[i],z[i+1]);path.close();p.setColor(color);c.drawPath(path,p);}
 void button(String s,float x,float y,float w,float h,int color){round(x,y,w,h,color);txt(s,x+w/2,y+h/2+8,Math.min(25,(int)(w/Math.max(7,s.length())*1.5)),0xffeef8ff);}
 void update(float dt){
   toastTime-=dt;signal+=dt;if(screen!=0)return;
   float desired=(right?1:0)-(left?1:0);steer+=(desired-steer)*Math.min(1,dt*6);
   if(gear==3&&!hand) speed+=((gas?56:0)-(brake?115:10+speed*.045f))*dt;
   else if(gear==1&&!hand)speed+=((gas?35:0)-(brake?105:12+speed*.08f))*dt;
   else speed-=dt*(hand?170:110);
   speed=Math.max(0,Math.min((car==10||car==11?230:car>=16?165:185)*(mode==4&&!awd?.65f:1),speed));
   lane+=steer*speed*dt*.038f*(gear==1?-1:1);lane=Math.max(-3,Math.min(3,lane));
   float moved=speed*dt/3.6f*(Math.abs(lane)>1.8?.58f:1);
   distance+=moved*(gear==1?-1:1);
   if(mode==1&&mission){
     progress+=Math.max(0,moved);opponent+=dt*(8.5f+level*.9f);
     if(progress>=target){mission=false;boolean win=progress>opponent;int prize=win?450+level*200:80;money+=prize;if(win)level++;store();say(win?"RACE WIN +"+prize:"RACE FINISHED +80");}
   }
   if(mode==2&&mission&&progress<target){
     progress+=Math.max(0,moved);if(progress>=target&&speed<20){money+=330;trip++;mission=false;store();say("PASSENGER DELIVERED +330");}
   }
   if(mode==3&&mission){
      if(lesson==0&&gear==3){lesson++;say("GOOD! ACCELERATE TO 35");}
      else if(lesson==1&&speed>=35){lesson++;say("SLOW DOWN TO STOP");}
      else if(lesson==2&&speed<1&&brake){lesson++;say("SHIFT TO P, APPLY HANDBRAKE");}
      else if(lesson==3&&gear==0&&hand){lesson=0;mission=false;money+=250;store();say("LESSON PASSED +250");}
   }
   if(mode==0 && stage==0 && signal%22>12 && signal%22<14 && speed>65 && Math.abs(lane)<1){trafficFines++;money=Math.max(0,money-30);store();say("TRAFFIC LIGHT FINE -30");}
 }
 void world(){
   boolean mountain=mode==4||stage==1;
   rect(0,0,1100,620,0xff84bee7);circ(925,83,42,0xffffe9b0);
   rect(0,240,1100,380,mountain?0xff83a27c:0xff79a596);
   for(int i=0;i<8;i++){float x=i*185-((distance*.018f)%185);poly(mountain?0xff688e72:0xff8ca5ac,x-175,265,x+45,102+(i%3)*25,x+240,265);}
   if(!mountain)for(int i=0;i<16;i++){float x=i*95-(distance*.12f%95);int h=55+i%5*13;rect(x,253-h,68,h,0xffb8bcc3);for(int j=0;j<3;j++){rect(x+10+j*18,267-h,9,13,0xff6d95af);rect(x+10+j*18,286-h,9,13,0xff6d95af);}}
   for(int k=0;k<34;k++){
      float z=k*15,zz=(k+1)*15,y=267+z*.82f,yy=267+zz*.82f;
      float cx=550-lane*116+(float)Math.sin((distance+z)*.002)*z*.065f,cc=550-lane*116+(float)Math.sin((distance+zz)*.002)*zz*.065f;
      float w=65+z*.54f,ww=65+zz*.54f;
      poly(mountain?0xffaa9071:0xffa9b1b0,cx-w-20,y,cx+w+20,y,cc+ww+23,yy,cc-ww-23,yy);
      poly(mountain?0xff715d4e:0xff343944,cx-w,y,cx+w,y,cc+ww,yy,cc-ww,yy);
      if(!mountain){line(cx-w,y,cc-ww,yy,0xffe6eef4,3);line(cx+w,y,cc+ww,yy,0xffe6eef4,3);}
      if(k%4==0 && (int)((distance+z)/50)%2==0)poly(0xffffe4ab,cx-2,y,cx+2,y,cc+5,yy,cc-5,yy);
   }
   for(int i=0;i<10;i++){
      float z=(i*91-(distance*.7f%91)+910)%760,y=267+z*.55f,sc=.2f+z/700;
      if(y>608)continue;
      float x=550-lane*116+(i%2==0?-1:1)*(104+z*.6f);
      if(mountain){poly(0xff436f49,x-28*sc,y,x,y-90*sc,x+28*sc,y);rect(x-4*sc,y-9*sc,8*sc,16*sc,0xff614b36);}
      else{rect(x-9*sc,y-65*sc,18*sc,67*sc,0xff5a6a6d);circ(x,y-68*sc,14*sc,0xffddc8a5);line(x,y,x-15*sc,y+12*sc,0xff263b4a,3);line(x,y,x+14*sc,y+10*sc,0xff263b4a,3);}
   }
   if(!mountain){
    float phase=signal%22;float x=860,y=224;
    rect(x,y,8,105,0xff4c575b);round(x-16,y-38,40,82,0xff29343b);
    circ(x+4,y-20,10,phase<9?0xfff1443c:0xff603a35);
    circ(x+4,y+3,10,phase>=9&&phase<12?0xffffc348:0xff6f6336);
    circ(x+4,y+26,10,phase>=12?0xff43eb8c:0xff3b624e);
    rect(145,250,57,8,0xffc9cdca);round(138,214,71,40,0xffc3ddf0);txt("50",173,242,27,0xffc23332);
   }
   for(int i=0;i<5;i++){
      float z=(i*170+120-(distance*.66f%850)+850)%850;
      if(z<120||z>520)continue;float sc=.27f+z/550;
      float x=550-lane*116+(i%2==0?-1:1)*(40+z*.17f),y=267+z*.68f;
      round(x-34*sc,y-62*sc,68*sc,72*sc,0xff172733);
      round(x-30*sc,y-58*sc,60*sc,64*sc,cols[(i*5+6)%cols.length]);
      rect(x-23*sc,y-43*sc,46*sc,19*sc,0xff82a9c1);
      rect(x-30*sc,y+1*sc,13*sc,8*sc,0xffc43b34);
      rect(x+17*sc,y+1*sc,13*sc,8*sc,0xffc43b34);
   }
   float x=550+steer*20;
   p.setColor(0x77000000);c.drawOval(x-82,555,x+82,580,p);
   round(x-80,454,160,120,0xff17212c);round(x-71,458,142,102,cols[car]);
   poly(0xff769eb9,x-51,474,x+51,474,x+63,514,x-63,514);
   line(x-62,518,x+62,518,0xff374e60,5);
   round(x-68,535,29,13,0xffdf3434);round(x+39,535,29,13,0xffdf3434);
   rect(x-25,544,50,14,0xff222e39);
   txt(cars[car],550,454,17,0xff192b3b);
 }
 void ui(){
   round(10,10,290,93,0xd7223040);txt("CITY DRIVE 20",154,41,30,0xffffd26a);txt("CASH "+money+"  |  LV "+level,155,76,21,0xfff1faff);
   round(317,10,450,72,0xe31a3047);txt((int)speed+" KM/H",421,58,35,0xffebf7ff);txt(new String[]{"P","R","N","D"}[gear],573,58,40,0xffffd366);txt(modes[mode],689,53,22,0xffaff5d7);
   button("MENU",912,10,173,55,0xff4b5d79);
   if(mode==0)button(stage==0?"TO MOUNTAINS":"TO CITY",772,90,190,47,0xff316c73);
   if(mode==1) {round(345,94,393,35,0xee243849);rect(353,101,Math.min(374,progress/target*374),20,0xfff5bf52);txt("RACE "+(int)(100*Math.min(1,progress/target))+"%",546,121,20,0xffffffff);}
   if(mode==2)button(mission?"PASSENGER ON BOARD":"PICK UP TAXI FARE",715,95,367,45,0xff8b713c);
   if(mode==3)txt("LESSON: "+new String[]{"SHIFT INTO D","REACH 35 KM/H","BRAKE TO STOP","PARK + HANDBRAKE"}[Math.min(3,lesson)],520,165,23,0xffffe1a5);
   if(mode==4){button("4X4 "+(awd?"ON":"OFF"),12,107,175,52,awd?0xff398a73:0xff58637b);}
   button("HANDBRAKE "+(hand?"ON":"OFF"),12,167,208,53,hand?0xff9f494c:0xff435267);
   txt("GEARBOX",62,254,20,0xfff6f9fb);
   for(int g=0;g<4;g++)button(new String[]{"P","R","N","D"}[g],12,267+g*61,84,54,g==gear?0xffbb9150:0xff3c536a);
   circ(213,479,95,0xb5223343);p.setColor(0xffc8d6df);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(11);c.drawCircle(213,479,65,p);p.setStyle(Paint.Style.FILL);
   line(213,479,213+steer*53,417,0xffd2e1ed,11);line(213,479,155,512,0xffd2e1ed,9);line(213,479,271,512,0xffd2e1ed,9);circ(213,479,22,0xff526c7f);
   button("LEFT",100,559,115,50,left?0xff37809a:0xff536d85);button("RIGHT",230,559,116,50,right?0xff37809a:0xff536d85);
   button("BRAKE",771,464,136,143,brake?0xffb14d47:0xff6a4b53);button("GAS",918,417,167,190,gas?0xff48a677:0xff3a7958);
   if(toastTime>0){round(330,189,458,54,0xea203447);txt(toast,559,225,25,0xffffdf99);}
   if(gear==0&&speed<1)txt("PARKED",560,415,27,0xffd9fff5);
 }
 void home(){
   rect(0,0,1100,620,0xff172436);
   for(int k=0;k<7;k++)poly(0xff243f56,k*183-60,440,k*183+30,205,k*183+110,440);
   txt(screen==1?"CITY DRIVE 20":screen==2?"YOUR GARAGE":"SELECT CAR",550,79,52,0xffffd16f);
   txt("OFFLINE DRIVING EXPERIENCE",550,110,21,0xffb4daed);
   if(screen==1){
      for(int i=0;i<5;i++){int y=151+i*82;button(modes[i],270,y,560,68,i==mode?0xff4b8190:0xff334a64);}
      button("GARAGE  "+cars[car],260,574,580,40,0xff916d42);
      txt("SELECT MODE TO DRIVE",550,146,20,0xffb4d9d5);
   }else{
      for(int i=0;i<24;i++){int row=i/4,col=i%4;float xx=38+col*265,yy=127+row*67;int clr=car==i?0xff527f95:0xff293e54;round(xx,yy,250,60,clr);circ(xx+23,yy+21,12,cols[i]);txt(cars[i],xx+130,yy+22,18,0xffeff8fc);txt((owned&(1<<i))!=0?(car==i?"SELECTED":"OWNED"):(price[i]+" COINS"),xx+128,yy+49,15,0xffffd38d);}
      txt("BALANCE "+money,550,561,24,0xffa5eac4);button("BACK",426,578,245,36,0xff976d51);
   }
 }
 @Override protected void onDraw(Canvas cv){
   c=cv;sx=getWidth()/1100f;sy=getHeight()/620f;
   float dt=Math.min(.06f,(System.nanoTime()-last)/1e9f);last=System.nanoTime();update(dt);
   c.save();c.scale(sx,sy);if(screen==0){world();ui();}else home();c.restore();postInvalidateDelayed(16);
 }
 boolean hit(float x,float y,float a,float b,float w,float h){return x>=a&&x<=a+w&&y>=b&&y<=b+h;}
 void down(float x,float y){
   if(screen==1){for(int i=0;i<5;i++)if(hit(x,y,270,151+i*82,560,68)){mode=i;screen=0;stage=i==4?1:0;speed=0;gear=0;hand=false;progress=0;mission=i==1||i==3;target=450+level*110;opponent=0;say("MODE: "+modes[i]);return;}if(hit(x,y,260,574,580,43)){screen=2;return;}return;}
   if(screen==2){for(int i=0;i<24;i++){int row=i/4,col=i%4;if(hit(x,y,38+col*265,127+row*67,250,60)){if((owned&(1<<i))!=0){car=i;store();say("CAR SELECTED");}else if(money>=price[i]){money-=price[i];owned|=1<<i;car=i;store();say("PURCHASED");}else say("NOT ENOUGH MONEY");return;}}if(hit(x,y,426,578,245,42))screen=1;return;}
   if(hit(x,y,912,10,173,55)){screen=1;gas=false;brake=false;return;}
   if(mode==0&&hit(x,y,772,90,190,47)){stage=1-stage;speed=0;return;}
   if(mode==2&&hit(x,y,715,95,367,45)){if(!mission){mission=true;target=200+trip*40;progress=0;say("DRIVE PASSENGER TO DESTINATION");}else if(progress>=target&&speed<20){money+=330;trip++;mission=false;store();say("TAXI FARE +330");}return;}
   if(mode==4&&hit(x,y,12,107,175,52)){awd=!awd;return;}
   if(hit(x,y,12,167,208,53)){hand=!hand;return;}
   for(int i=0;i<4;i++)if(hit(x,y,12,267+i*61,84,54)){if(speed<4){gear=i;say("GEAR "+new String[]{"P","R","N","D"}[i]);}else say("STOP FIRST TO SHIFT");return;}
   if(hit(x,y,100,559,115,50))left=true;if(hit(x,y,230,559,116,50))right=true;
   if(hit(x,y,771,464,136,143))brake=true;if(hit(x,y,918,417,167,190))gas=true;
 }
 public boolean onTouchEvent(MotionEvent e){
   int action=e.getActionMasked(),index=e.getActionIndex();
   if(action==MotionEvent.ACTION_DOWN||action==MotionEvent.ACTION_POINTER_DOWN){down(e.getX(index)/sx,e.getY(index)/sy);return true;}
   if(action==MotionEvent.ACTION_UP||action==MotionEvent.ACTION_CANCEL){gas=false;brake=false;left=false;right=false;return true;}
   if(action==MotionEvent.ACTION_POINTER_UP){float x=e.getX(index)/sx,y=e.getY(index)/sy;if(hit(x,y,100,559,115,50))left=false;if(hit(x,y,230,559,116,50))right=false;if(hit(x,y,771,464,136,143))brake=false;if(hit(x,y,918,417,167,190))gas=false;return true;}
   return true;
 }
 }
}