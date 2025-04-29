package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class LoginTabFragement extends Fragment {

    TextView pass;

    TextView email;
    int v=0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancesState){
        ViewGroup root=(ViewGroup) inflater.inflate(R.layout.login_tab_fragment,container,false);
        email=root.findViewById(R.id.email);
        pass=root.findViewById(R.id.pass);
        email .setTranslationX(300);
        email.setAlpha(v);
        email.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(400).start();

        pass .setTranslationX(300);
        pass.setAlpha(v);
        pass.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(400).start();


        return root;
    }
}
