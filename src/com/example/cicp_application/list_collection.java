package com.example.cicp_application;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class list_collection extends Fragment {
	   @Override
	      public View onCreateView(LayoutInflater inflater, ViewGroup container,
	              Bundle savedInstanceState) {
	          View ios = inflater.inflate(R.layout.list_collection, container, false);
	          ((TextView)ios.findViewById(R.id.textView)).setText("Your Collection!");
	          return ios;
	}}
