package beaconfinder.fun.berger.de.beaconfinder.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import beaconfinder.fun.berger.de.beaconfinder.R;

/**
 * About Me Fragment.
 */
public class AboutFragment extends Fragment {
    private String email;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        //init email string
        email = getString(R.string.email_berger_apps);

        //EMail Button
        Button contactButton = (Button) view.findViewById(R.id.contactButton);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", email, null));
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email applications installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
