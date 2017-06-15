package turn.zio.zara.travel_log;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by 하루마다 on 2017-06-14.
 */

class MainAdapter extends BaseAdapter {

    private final int layout;
    Context context;

    LayoutInflater mInflater;
    String[] title;
    LayoutInflater inf;
    String[] Content;
    String[] date;
    String[] writeuser_id;
    String[] file_type;
    String[] adress;

    Drawable drawable;

    private Bitmap[] images;
    //생성자
    public MainAdapter(Context context,int layout, String[] title, String[] Content, String[] date, String[] writeuser_id, String[] file_type, String[] adress) {
        //인플레이트 준비를 합니다.
        this.context = context;
        this.layout = layout;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.title = title;
        this.Content = Content;
        this.date = date;
        this.writeuser_id = writeuser_id;
        this.file_type = file_type;
        this.adress = adress;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return title.length;
    }

    @Override
    public Object getItem(int position) {
        return title[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void image(Bitmap[] images){
        this.images = images;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //최초 호출이면 항목 뷰를 생성한다.
        //타입별로 뷰를 다르게 디자인 할 수 있으며 높이가 달라도 상관없다.
        if(convertView == null)
            //인플레이트합니다. 즉 화면에 뿌립니다.
            convertView = inf.inflate(layout, null);

        //화면에 뿌린뒤 여기서 각항목에 해당하는 값을 바꿔주는 부분입니다.

        TextView titles = (TextView)convertView.findViewById(R.id.log_title);
        titles.setText(title[position]);
        TextView Contents = (TextView)convertView.findViewById(R.id.log_cotennt);
        Contents.setText(Content[position]);
        TextView log_place = (TextView)convertView.findViewById(R.id.log_place);
        log_place.setText(adress[position]);
        TextView log_date = (TextView)convertView.findViewById(R.id.log_date);
        log_date.setText(date[position]);
        TextView user_id = (TextView)convertView.findViewById(R.id.user_id);
        user_id.setText(writeuser_id[position]);

        ImageView iv = (ImageView)convertView.findViewById(R.id.log_picture);
        LinearLayout picView = (LinearLayout)convertView.findViewById(R.id.log_picture_Linear);

        float mScale = context.getResources().getDisplayMetrics().density;

        if (file_type[position].equals("1")) {
            picView.setVisibility(View.VISIBLE);
            iv.setImageBitmap(images[position]);
            int calHeight = (int)(40*mScale);
            Contents.setHeight(calHeight);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
        } else if(file_type[position].equals("2")){
            picView.setVisibility(View.VISIBLE);
            int calHeight = (int)(40*mScale);
            Contents.setHeight(calHeight);
            drawable = context.getResources().getDrawable(R.drawable.voice);
            iv.setImageDrawable(drawable);
        }else{
            int calHeight = (int)(80*mScale);
            Contents.setHeight(calHeight);
            picView.setVisibility(View.GONE);
        }
        return convertView;//getCount만큼 반복한다고 했죠?
        //리스트의 갯수만큼 반복하게 됩니다.
    }


}
