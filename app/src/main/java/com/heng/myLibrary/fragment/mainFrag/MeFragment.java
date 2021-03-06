package com.heng.myLibrary.fragment.mainFrag;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.heng.myLibrary.R;
import com.heng.myLibrary.activity.LoginActivity;
import com.heng.myLibrary.activity.NewsActivity;
import com.heng.myLibrary.adapter.MeItemAdapter;
import com.heng.myLibrary.database.bean.MeItemBean;
import com.heng.myLibrary.database.entity.User;
import com.heng.myLibrary.util.MyLogging;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * ๆ็้กต้ข
 */
public class MeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MeFragment";
    CircleImageView iconIv;
    TextView nameTv, codeTv;
    User user;
    ListView meLv;
    List<MeItemBean> mDatas;
    Button exitBtn, newsBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyLogging.myLog(TAG, "onCreate()");

        mDatas = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_1_me, container, false);

        MyLogging.myLog(TAG, "onCreateView()");

        //todo: ่ทๅๆฐๆฎ
        Bundle bundle = getArguments();
        assert bundle != null;
        user = (User) bundle.getSerializable("userInfo");

        initView(view);
        addDataToList();
        loadUserImg();

        //todo: listview่ฎพ็ฝฎ่งฆๅไบไปถ
        setLvListener();

        return view;
    }

    private void setLvListener() {
        MyLogging.myLog(TAG, "setLvListener()");

        meLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // ๆ็็งฏๅ
                        Toast.makeText(getContext(), "ๅฝๅ็งฏๅ๏ผ" + user.getUserCode(), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        //ๆ็ๅไนฆ
//                        Toast.makeText(getContext(), "ไนฆๅ๏ผ  " + user.getBookName(), Toast.LENGTH_SHORT).show();
                        if (user.getBookName() == null) {
                            Toast.makeText(getContext(), "ๆจๆชๅไนฆ", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getContext(), "ไนฆๅ๏ผ" + user.getBookName(), Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
            }

        });
    }

    private void initView(View view) {
        MyLogging.myLog(TAG, "initView()");

        iconIv = view.findViewById(R.id.meFrag_iv);
        nameTv = view.findViewById(R.id.user_name);
        codeTv = view.findViewById(R.id.user_code);
        meLv = view.findViewById(R.id.me_lv);
        exitBtn = view.findViewById(R.id.exit_login);
        newsBtn = view.findViewById(R.id.news);

        exitBtn.setOnClickListener(this);
        newsBtn.setOnClickListener(this);

        //todo:่ฎพ็ฝฎ็ๅฌๅจ๏ผ้ๆฉ็ธๅๅพ็ๅฝๅคดๅ
        iconIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                MyLogging.myLog(TAG, "่ทๅ็ธๅๅพ็่ฎพ็ฝฎๅคดๅ()");

                //todo:ๅจๆ็ณ่ฏทๆ้
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission
                        .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    //todo: ๆง่กๅฏๅจ็ธๅ็ๆนๆณ
                    openAlbum();
                }
            }
        });
        nameTv.setText(user.getUserName());
        codeTv.setText(String.valueOf(user.getUserCode()));
    }

    //todo: ๅฏๅจ็ธๅ็ๆนๆณ
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    //todo:่ทๅๆ้็็ปๆ
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openAlbum();
            else Toast.makeText(getContext(), "ไฝ?ๆ็ปไบ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2) {
            //ๅคๆญๅฎๅ็ๆฌ
            if (resultCode == Activity.RESULT_OK && data != null) {
                if (Build.VERSION.SDK_INT >= 19)
                    handImage(data);
                else handImageLow(data);
            }
        }
    }

    //todo: ๅฎๅ็ๆฌๅคงไบ4.4็ๅค็ๆนๆณ
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handImage(Intent data) {
        String path = null;
        Uri uri = data.getData();
        //ๆ?นๆฎไธๅ็uri่ฟ่กไธๅ็่งฃๆ
        if (DocumentsContract.isDocumentUri(getContext(), uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                path = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                path = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            path = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }
        //todo: ๅฑ็คบๅพ็
        displayImage(path);
    }

    //todo: ๅฎๅๅฐไบ4.4็ๅค็ๆนๆณ
    private void handImageLow(Intent data) {
        Uri uri = data.getData();
        String path = getImagePath(uri, null);
        displayImage(path);
    }

    //todo: content็ฑปๅ็uri่ทๅๅพ็่ทฏๅพ็ๆนๆณ
    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContext().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //todo: ๆ?นๆฎ่ทฏๅพๅฑ็คบๅพ็็ๆนๆณ
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            iconIv.setImageBitmap(bitmap);
        } else {
            Toast.makeText(getContext(), "fail to set image", Toast.LENGTH_SHORT).show();
        }
    }

    // todo: listๆทปๅ?ๆฐๆฎ
    private void addDataToList() {
        MeItemBean userCode = new MeItemBean("ๆ็็งฏๅ", "", "");
        MeItemBean inLibTime = new MeItemBean("ๅจ้ฆๆถ้ด", "", "");
        MeItemBean collection = new MeItemBean("ๆ็ๆถ่", "", "");
        MeItemBean bookReview = new MeItemBean("ๆ็ไนฆ่ฏ", "", "");
        MeItemBean bookLend = new MeItemBean("ๆๅ็ไนฆ", "", "");
        MeItemBean setting = new MeItemBean("่ฎพ็ฝฎ", "", "");

        mDatas.add(userCode);
        mDatas.add(bookLend);
        mDatas.add(inLibTime);
        mDatas.add(collection);
        mDatas.add(bookReview);
        mDatas.add(setting);

        //todo:  ่ฎพ็ฝฎ้้ๅจ
        MeItemAdapter adapter = new MeItemAdapter(getContext(), mDatas);
        meLv.setAdapter(adapter);
    }

    //todo: ่ฎพ็ฝฎ้ป่ฎคๅคดๅ
    private void loadUserImg() {
        if (user != null) {
            switch (user.getSex()) {
                case "็ท":
                    iconIv.setImageResource(R.mipmap.man);
                    break;
                case "ๅฅณ":
                    iconIv.setImageResource(R.mipmap.woman);
                    break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.exit_login) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            Objects.requireNonNull(getActivity()).finish();
        } else if (view.getId() == R.id.news) {
            Intent intent = new Intent(getContext(), NewsActivity.class);
            startActivity(intent);
        }
    }
}