package com.wainaina.livelator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import static com.wainaina.livelator.home.audioOn;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Translated_Speech.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Translated_Speech#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Translated_Speech extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Audio audioController;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Translated_Speech() {
        // Required empty public constructor

        audioController = new Audio();
        audioController.initializeRecording();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Translated_Speech.
     */
    // TODO: Rename and change types and number of parameters
    public static Translated_Speech newInstance() {
       // Translated_Speech fragment = new Translated_Speech();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return new Translated_Speech();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_translated__speech, container, false);
        listen_to_fragment_events(view);

        return view;
    }

    //create event listeners to fragment views
    private void listen_to_fragment_events(View view) {
        play_pause_button(view);

    }

    private void play_pause_button(View view){
        ImageView play_pause_s;
        play_pause_s=(ImageView)view.findViewById(R.id.play_pause_s);
        play_pause_s.setTag("play");

        play_pause_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_play_pause(view);
            }
        });
    }

    private void change_play_pause(View view) {

        //check the view and change accordingly
        ImageView play_pause = (ImageView)view;
        Integer play_pause_id = view.getId();

        if(play_pause_id==R.id.play_pause_s){
            if(play_pause.getTag().equals("play")){
                play_pause.setImageResource(R.mipmap.pause_circle);
                play_pause.setTag("pause");
                audioOn = true;
                audioController.startStreaming();

            }
            else{
                play_pause.setImageResource(R.mipmap.play_circle);
                play_pause.setTag("play");
                audioOn = false;
                audioController.recorder.release();
            }

        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(View view) {
        if (mListener != null) {
            mListener.translated_Speech_interface_callback();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void translated_Speech_interface_callback();
    }
}
