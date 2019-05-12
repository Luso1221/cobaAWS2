package my.application.cobaaws2;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import org.w3c.dom.Text;

import java.util.List;

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayout layoutManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View myFragmentView = inflater.inflate(R.layout.history_fragment, container, false);

        return myFragmentView;
    }
    public class HistoryAdapter extends RecyclerView.Adapter<HistoryFragment.HistoryAdapter.ViewHolder> {

        private List<Synchronization> mSync;
        private Context context;

        public HistoryAdapter(List<Synchronization> sync) {
            mSync = sync;
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView status,lastSync;
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public Toolbar toolbar;
            public TextView profileName;
            public TextView timeStampStart, timeStampEnd;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View itemView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(itemView);
                timeStampStart = (TextView)itemView.findViewById(R.id.startTime);
                timeStampEnd= (TextView)itemView.findViewById(R.id.endTime);
                toolbar= (Toolbar) itemView.findViewById(R.id.toolbar);
                status= (TextView) itemView.findViewById(R.id.status);
                lastSync = (TextView) itemView.findViewById(R.id.lastSynced);
            }
        }

        @NonNull
        @Override
        public HistoryFragment.HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            final Context context = parent.getContext();

            // Inflate the custom layout
            View layout = LayoutInflater.from(context).inflate(R.layout.history_list_item, parent, false);

            // Return a new holder instance
            HistoryFragment.HistoryAdapter.ViewHolder viewHolder = new HistoryFragment.HistoryAdapter.ViewHolder(layout);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryFragment.HistoryAdapter.ViewHolder holder, int position) {

            Synchronization synchronization = mSync.get(position);

            MyDB myDB = new MyDB(getActivity());
            holder.profileName.setText(myDB.getProfile(synchronization.getProfile_id()).getProfile_name());
            holder.timeStampStart.setText(synchronization.getTimestamp_start().toString());
            holder.timeStampEnd.setText(synchronization.getTimestamp_end().toString());
            holder.lastSync.setText(myDB.getLastSynced(synchronization.getSync_id()).getFileData().getFileName());
            holder.toolbar.inflateMenu(R.menu.menu3);
            holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch(item.getItemId()){
                        case R.id.detail:
                            Intent i = new Intent(getActivity(),DetailActivity.class);
                            i.putExtra("profile_id",synchronization.getProfile_id());
                            startActivity(i);
                            break;
                    }
                    return true;
                }
            });
            // Set item views based on your views and data model
        }

        @Override
        public int getItemCount() {
            return mSync.size();
        }


    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

}
