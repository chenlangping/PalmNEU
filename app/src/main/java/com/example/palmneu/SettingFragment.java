package com.example.palmneu;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View settingLayout = inflater.inflate(R.layout.setting_layout,
				container, false);
		return settingLayout;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Button button = (Button) getActivity().findViewById(R.id.connect_wifi);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent =new Intent(getActivity(),WifiLoginIn.class);
				startActivity(intent);
			}
		});

		Button button2 = (Button) getActivity().findViewById(R.id.save_user_information);
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent =new Intent(getActivity(),SaveUserData.class);
				startActivity(intent);
			}
		});

		Button button3 = (Button) getActivity().findViewById(R.id.login_palm_neu);
		button3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent =new Intent(getActivity(),UserLogin.class);
				startActivity(intent);
			}
		});
	}
}
