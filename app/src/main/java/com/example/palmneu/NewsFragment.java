package com.example.palmneu;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NewsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View newsLayout = inflater.inflate(R.layout.news_layout, container,
				false);
		return newsLayout;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Button button = (Button) getActivity().findViewById(R.id.getgrade);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(getActivity(),LoginIn.class);
				startActivity(intent);
			}
		});

		Button button1 = (Button)getActivity().findViewById(R.id.ecard);
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getActivity(),Ecard.class);
				startActivity(intent);
			}
		});

		Button button3 = (Button)getActivity().findViewById(R.id.getlibrary);
		button3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getActivity(),Library.class);
				startActivity(intent);
			}
		});
	}

}
