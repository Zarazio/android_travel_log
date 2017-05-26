package turn.zio.zara.travel_log;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

public class AlbumSelectActivity extends AppCompatActivity {

    GalleryAdapter mAdapter ;
    RecyclerView mRecyclerView ;

    ArrayList<File> list ;
    ArrayList<ImageModel> data = new ArrayList<>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_select);

        list = imageReader(Environment.getExternalStorageDirectory()) ;



        for(int i=0 ; i<list.size() ; i++){
            ImageModel imageModel = new ImageModel() ;
            imageModel.setName("Image " + i);
            imageModel.setUrl(list.get(i)+"");
            data.add(imageModel)  ;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar) ;
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.list) ;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new GalleryAdapter(AlbumSelectActivity.this, data) ;
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener(){

            @Override
            public void onItemClick(View view, int position){
                Intent intent = new Intent(AlbumSelectActivity.this, DetailActivity.class) ;
                intent.putParcelableArrayListExtra("data", data) ;
                intent.putExtra("pos", position) ;
                startActivity(intent);
            }
        }));


    }


    ArrayList<File> imageReader(File root){

        ArrayList<File> a = new ArrayList<>() ;

        File[] files = root.listFiles() ;
        for(int i=0 ; i < files.length ; i++){
            if(files[i].isDirectory()) {
                a.addAll(imageReader(files[i])) ;
            }else{
                if(files[i].getName().endsWith(".jpg")) {
                    a.add(files[i]) ;
                }
            }
        }
        return a ;
    }
}
