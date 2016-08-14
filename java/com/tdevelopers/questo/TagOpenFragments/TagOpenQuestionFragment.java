package com.tdevelopers.questo.TagOpenFragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tdevelopers.questo.Adapters.QuestionRecyclerAdapter;
import com.tdevelopers.questo.Objects.Question;
import com.tdevelopers.questo.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class TagOpenQuestionFragment extends Fragment {

    String id = "";
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    AVLoadingIndicatorView avl;

    public TagOpenQuestionFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public TagOpenQuestionFragment(String id) {
        // Required empty public constructor
        this.id = id;
    }

    public static TagOpenQuestionFragment newInstance(String id) {

        Bundle args = new Bundle();

        TagOpenQuestionFragment fragment = new TagOpenQuestionFragment(id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tag_open_question, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.tagopenquestionrv);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        getData();
        avl = (AVLoadingIndicatorView) view.findViewById(R.id.avloading);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    public void getData() {
        try {

            if (id != null && id.trim().length() != 0) {
                Query query = FirebaseDatabase.getInstance().getReference("TagUploads").child(id + "").child("Questions");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, Boolean> tobesent = new HashMap<String, Boolean>();
                        final ArrayList<Question> data = new ArrayList<Question>();
                        final QuestionRecyclerAdapter adapter = new QuestionRecyclerAdapter(data);
                        recyclerView.setAdapter(adapter);
                        if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                            tobesent = ((HashMap<String, Boolean>) dataSnapshot.getValue());
                            if (tobesent != null) {
                                if (tobesent.size() == 0) {
                                    avl.setVisibility(View.GONE);
                                }
                                for (String s : tobesent.keySet()) {

                                    FirebaseDatabase.getInstance().getReference("Question").child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            avl.setVisibility(View.GONE);
                                            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                                                Question q = (Question) dataSnapshot.getValue(Question.class);

                                                if (q != null) {
                                                    data.add(q);
                                                    adapter.notifyItemInserted(data.size());
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        } catch (Exception e) {

        }
    }
}
