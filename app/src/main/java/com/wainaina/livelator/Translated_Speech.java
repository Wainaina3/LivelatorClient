package com.wainaina.livelator;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.android.speech_to_text.v1.ISpeechDelegate;
import com.ibm.watson.developer_cloud.android.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.android.speech_to_text.v1.dto.SpeechConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import static android.content.ContentValues.TAG;
import static com.wainaina.livelator.home.audioOn;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Translated_Speech.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Translated_Speech#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Translated_Speech extends Fragment implements ISpeechDelegate {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

   // private Audio audioController;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View view;

    // session recognition results
    private static String mRecognitionResults = "";

    private enum ConnectionState {
        IDLE, CONNECTING, CONNECTED
    }

    ConnectionState mState = ConnectionState.IDLE;
    public Context mContext = null;
    public static JSONObject jsonModels = null;
    private Handler mHandler = null;

    private Spinner mSpinner;


    public Translated_Speech() {
        // Required empty public constructor

//        audioController = new Audio();
//        audioController.initializeRecording();
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
        view = inflater.inflate(R.layout.fragment_translated__speech, container, false);
        listen_to_fragment_events(view);
        mContext = getActivity().getApplicationContext();
        mHandler = new Handler();

        //start initializing stt
        if (initSTT() == false) {
            displayResult("Error: no authentication credentials/token available, please enter your authentication information");
            return view;
        }

        if (jsonModels == null) {
            STTModelsCommand();
            if (jsonModels == null) {
                displayResult("Please, check internet connection.");
                return view;
            }
        }
       // addItemsOnSpinnerModels();

        displayResult("please..status, press the button to start speaking");

        ImageView play_pause_s = (ImageView) view.findViewById(R.id.play_pause_s);
        play_pause_s.setTag("play");
        play_pause_s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                change_play_pause(view);
            }
        });

        //end initilizing stt
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
            if(play_pause.getTag().equals("play")){
                play_pause.setImageResource(R.mipmap.pause_circle);
                play_pause.setTag("pause");
                audioOn = true;
                //Do something
                if (mState == ConnectionState.IDLE) {
                    mState = ConnectionState.CONNECTING;
                    Log.d(TAG, "onClickPlay: IDLE -> CONNECTING");
                   // Spinner spinner = (Spinner)view.findViewById(R.id.from_lang_s);
                    //spinner.setEnabled(false);
                    mRecognitionResults = "";
                    displayResult(mRecognitionResults);
                    ItemModel item = (ItemModel)mSpinner.getSelectedItem();
                    SpeechToText.sharedInstance().setModel(item.getModelName());
                    displayResult("Status: connecting to the STT service...");
                    // start recognition
                    new AsyncTask<Void, Void, Void>(){
                        @Override
                        protected Void doInBackground(Void... none) {
                            SpeechToText.sharedInstance().recognize();
                            return null;
                        }
                    }.execute();
                    // setButtonLabel(R.id.buttonRecord, "Connecting...");
                    //setButtonState(true);
                }

            }
            else if (mState == ConnectionState.CONNECTED) {
                play_pause.setImageResource(R.mipmap.play_circle);
                play_pause.setTag("play");
                audioOn = false;
                //Do something
                mState = ConnectionState.IDLE;
                Log.d(TAG, "onClickRecord: CONNECTED -> IDLE ==disconnected");
               // Spinner spinner = (Spinner)view.findViewById(R.id.from_lang_s);
               // spinner.setEnabled(true);
                SpeechToText.sharedInstance().stopRecognition();
                // setButtonState(false);
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


    private String getModelSelected() {

        Spinner spinner = (Spinner)view.findViewById(R.id.from_lang_s);
        ItemModel item = (ItemModel)spinner.getSelectedItem();
        return item.getModelName();
    }

    public URI getHost(String url){
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    // initialize the connection to the Watson STT service
    private boolean initSTT() {

        // DISCLAIMER: please enter your credentials or token factory in the lines below
        String username = getString(R.string.sttusername);
        String password = getString(R.string.sttpassword);

        //String tokenFactoryURL = getString(R.string.defaultTokenFactory);
        String serviceURL = "https://stream.watsonplatform.net/speech-to-text/api";

        SpeechConfiguration sConfig = new SpeechConfiguration(SpeechConfiguration.AUDIO_FORMAT_OGGOPUS);
        //SpeechConfiguration sConfig = new SpeechConfiguration(SpeechConfiguration.AUDIO_FORMAT_DEFAULT);
        //sConfig.learningOptOut = false; // Change to true to opt-out

        SpeechToText.sharedInstance().initWithContext(this.getHost(serviceURL), getActivity().getApplicationContext(), sConfig);

        // token factory is the preferred authentication method (service credentials are not distributed in the client app)
//        if (tokenFactoryURL.equals(getString(R.string.defaultTokenFactory)) == false) {
//            SpeechToText.sharedInstance().setTokenProvider(new MyTokenProvider(tokenFactoryURL));
//        }
        // Basic Authentication
        if (username.equals(getString(R.string.defaultUsername)) == false) {
            SpeechToText.sharedInstance().setCredentials(username, password);
        } else {
            // no authentication method available
            return false;
        }

        SpeechToText.sharedInstance().setModel(getString(R.string.modelDefault));
        SpeechToText.sharedInstance().setDelegate(this);

        return true;
    }


    public class ItemModel {

        private JSONObject mObject = null;

        public ItemModel(JSONObject object) {
            mObject = object;
        }

        public String toString() {
            try {
                return mObject.getString("description");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        public String getModelName() {
            try {
                return mObject.getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void displayResult(final String result) {
        final Runnable runnableUi = new Runnable(){
            @Override
            public void run() {
                TextView textResult = (TextView)view.findViewById(R.id.textResult);
                textResult.setText(result);
            }
        };

        new Thread(){
            public void run(){
                mHandler.post(runnableUi);
            }
        }.start();
    }

    public void STTModelsCommand() {

        Thread streamThread = new Thread(new Runnable() {
            @Override
            public void run() {

                new STTModelsCommandClass().execute();
            }
        });

        streamThread.start();

    }

    //Get models
    private  class STTModelsCommandClass extends AsyncTask<Void, Void, JSONObject> {

        protected JSONObject doInBackground(Void... none) {

            jsonModels = SpeechToText.sharedInstance().getModels();
            return jsonModels;
        }
        protected void onPostExecute(JSONObject none) {
            addItemsOnSpinnerModels();
        }
    }

    //Add models to a spinner

    protected void addItemsOnSpinnerModels() {

       mSpinner = (Spinner)view.findViewById(R.id.from_lang_s);
        int iIndexDefault = 0;

        JSONObject obj = jsonModels;
        ItemModel [] items = null;
        try {
            JSONArray models = obj.getJSONArray("models");

            // count the number of Broadband models (narrowband models will be ignored since they are for telephony data)
            Vector<Integer> v = new Vector<>();
            for (int i = 0; i < models.length(); ++i) {
                if (models.getJSONObject(i).getString("name").indexOf("Broadband") != -1) {
                    v.add(i);
                   Log.d("i",models.getJSONObject(i).getString("name"));
                }
            }
            items = new ItemModel[v.size()];
            int iItems = 0;
            for (int i = 0; i < v.size() ; ++i) {
                items[iItems] = new ItemModel(models.getJSONObject(v.elementAt(i)));
                if (models.getJSONObject(v.elementAt(i)).getString("name").equals(getString(R.string.modelDefault))) {
                    iIndexDefault = iItems;
                }
                ++iItems;

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (items != null) {
            ArrayAdapter<ItemModel> spinnerArrayAdapter = new ArrayAdapter<ItemModel>(getActivity(), android.R.layout.simple_spinner_item, items);
            mSpinner.setAdapter(spinnerArrayAdapter);
            mSpinner.setSelection(iIndexDefault);
        }
    }




    // delegages ----------------------------------------------

    public void onOpen() {
        Log.d(TAG, "onOpen");
        displayResult("Status: successfully connected to the STT service");
        mState = ConnectionState.CONNECTED;
    }

    public void onError(String error) {

        Log.e(TAG, error);
        displayResult(error);
        mState = ConnectionState.IDLE;
    }

    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "onClose, code: " + code + " reason: " + reason);
        displayResult("Status: connection closed");
        mState = ConnectionState.IDLE;
    }

    public void onMessage(String message) {

        Log.d(TAG, "onMessage, message: " + message);
        try {
            JSONObject jObj = new JSONObject(message);
            // state message
            if(jObj.has("state")) {
                Log.d(TAG, "Status message: " + jObj.getString("state"));
            }
            // results message
            else if (jObj.has("results")) {
                //if has result
                Log.d(TAG, "Results message: ");
                JSONArray jArr = jObj.getJSONArray("results");
                for (int i=0; i < jArr.length(); i++) {
                    JSONObject obj = jArr.getJSONObject(i);
                    JSONArray jArr1 = obj.getJSONArray("alternatives");
                    String str = jArr1.getJSONObject(0).getString("transcript");
                    // remove whitespaces if the language requires it
                    String model = this.getModelSelected();
                    if (model.startsWith("ja-JP") || model.startsWith("zh-CN")) {
                        str = str.replaceAll("\\s+","");
                    }
                    String strFormatted = Character.toUpperCase(str.charAt(0)) + str.substring(1);
                    if (obj.getString("final").equals("true")) {
                        String stopMarker = (model.startsWith("ja-JP") || model.startsWith("zh-CN")) ? "。" : ". ";
                        mRecognitionResults += strFormatted.substring(0,strFormatted.length()-1) + stopMarker;

                        displayResult(mRecognitionResults);
                    } else {
                        displayResult(mRecognitionResults + strFormatted);
                    }
                    break;
                }
            } else {
                displayResult("unexpected data coming from stt server: \n" + message);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON");
            e.printStackTrace();
        }
    }

    public void onAmplitude(double amplitude, double volume) {
        //Logger.e(TAG, "amplitude=" + amplitude + ", volume=" + volume);
    }
}
