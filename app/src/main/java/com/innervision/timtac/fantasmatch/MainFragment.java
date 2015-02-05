package com.innervision.timtac.fantasmatch;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import java.util.Arrays;

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private UiLifecycleHelper uiHelper;

    private String lastname;
    private String firstname;
    private String birthday;
    private String gender;
    private String interest;
    private String email;
    private String id;
    private String status;
    private String relation;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        LoginButton authButton = (LoginButton) view.findViewById(R.id.auhtButton);
        authButton.setReadPermissions(Arrays.asList("user_status","public_profile","user_birthday","email","user_relationships","user_relationship_details","user_about_me"));
        authButton.setFragment(this);

        return view;
    }

    private void onSessionStateChange(Session session, final SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");

            // Request user data and show the results
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        // Display the parsed user info
                        //Toast.makeText(getActivity(),buildUserInfoDisplay(user),Toast.LENGTH_SHORT).show();

                        buildUserInfoDisplay(user);
                        Intent intent = new Intent(getActivity(),Profil_public.class);
                        intent.putExtra("lastname",lastname);
                        intent.putExtra("firstname", firstname);
                        intent.putExtra("birthday",birthday);
                        intent.putExtra("gender",gender);
                        intent.putExtra("interest",interest);
                        intent.putExtra("email",email);
                        intent.putExtra("id",id);
                        intent.putExtra("status",status);
                        startActivity(intent);
                    }
                }
            });

        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
        }
    }


    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private Session.StatusCallback statusCallback = new SessionStatusCallback();

    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this)
                    .setPermissions(Arrays.asList("public_profile"))
                    .setCallback(statusCallback));
        } else {
            Session.openActiveSession(getActivity(), this, true, statusCallback);
        }
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            // Respond to session state changes, ex: updating the view
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


    private String buildUserInfoDisplay(GraphUser user) {
        StringBuilder userInfo = new StringBuilder("");

        //Toast.makeText(getActivity(),"TEST" + user.toString(),Toast.LENGTH_LONG).show();

        userInfo.append(String.format("Birthday: %s\n\n",
                user.getBirthday()));

        userInfo.append(String.format("test :%s\n\n",
                user.getFirstName()));

        userInfo.append(String.format("test :%s\n\n",
                user.getId()));

        userInfo.append(String.format("test :%s\n\n",
                user.getLastName()));

        userInfo.append(String.format("test :%s\n\n",
                user.asMap().get("email")));

        userInfo.append(String.format("test :%s\n\n",
                user.getProperty("gender")));

        userInfo.append(String.format("test :%s\n\n",
                user.getProperty("interested_in")));

        userInfo.append(String.format("test :%s\n\n",
                user.asMap().get("relationship_status")));

        userInfo.append(String.format("test :%s\n\n",
                user.getProperty("significant_other.id")));

        userInfo.append(String.format("test :%s\n\n",
                user.getProperty("significant_other")));

        lastname = user.getLastName();
        firstname = user.getFirstName();
        birthday =  user.getBirthday();
        id = user.getId();
        email = user.getProperty("email").toString();
        gender = user.asMap().get("gender").toString();
        interest = user.getProperty("interested_in").toString();
        status = user.getProperty("relationship_status").toString();

        return userInfo.toString();

    }

}