package com.main.beyond.TiO;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerViewAll;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference,postsRef,userRef,messagesRef,currentUserIdMessagesRef;
    private TextView navUserName;
    public String receverFullName,currentUserId,currentCategory = "Сексуална Експлатација";
    private ImageButton addNewPostButton;
    private RelativeLayout relativeLayout;
    private SwipyRefreshLayout mainSwipeRefreshLayout;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int i = 1;


    String CurrentUserFirstName;
    String CurrentUserLastName;
    String CurrentUserFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null){
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        }
        mToolbar = findViewById(R.id.main_page_toolbar);



        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        addNewPostButton = findViewById(R.id.add_new_post);

        drawerLayout = findViewById(R.id.drawable_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,drawerLayout, R.string.drawer_open,R.string.drawer_closed);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigation_view);
        mainSwipeRefreshLayout = findViewById(R.id.main_swipeRefreshLayout);
        mainSwipeRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);


        recyclerViewAll = findViewById(R.id.viewCategoryAll);
        recyclerViewAll.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewAll.setLayoutManager(linearLayoutManager);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

        navUserName = navView.findViewById(R.id.nav_user_name);



        if(currentUserId!=null){

            databaseReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.hasChild("FirstName") || !dataSnapshot.hasChild("LastName") || !dataSnapshot.hasChild("DateofBirth")){

                        SendUserToSetupActivity();

                    }else {

                        CurrentUserFirstName = dataSnapshot.child("FirstName").getValue().toString();
                        CurrentUserLastName = dataSnapshot.child("LastName").getValue().toString();
                        CurrentUserFullName = CurrentUserFirstName + " " + CurrentUserLastName;

                        navUserName.setText(CurrentUserFullName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

               UserMenuSelector(item);

                return false;
            }
        });


        addNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToPostActivity();

            }
        });


        mainSwipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {

                i++;

                PopulateAll();

            }
        });



    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder{

        TextView hasHelper;
        TextView Username;
        TextView Date;
        TextView Time;
        TextView Content;
        TextView PostName;
        LinearLayout relativeLayout;
        ImageButton CommentButton;
        TextView ekstra;
        ImageButton messageButton;





        public PostsViewHolder(View itemView) {
            super(itemView);

            Username = itemView.findViewById(R.id.post_username);
            Date = itemView.findViewById(R.id.post_date);
            Time = itemView.findViewById(R.id.post_time);
            Content = itemView.findViewById(R.id.post_content);
            PostName = itemView.findViewById(R.id.post_category);
            CommentButton = itemView.findViewById(R.id.commentButton);
            relativeLayout = itemView.findViewById(R.id.allPostsLayoutID);
            ekstra = itemView.findViewById(R.id.post_ekstra);
            messageButton = itemView.findViewById(R.id.post_messageButton);



        }
    }

    public static class UserMessagesViewHolder extends RecyclerView.ViewHolder{

        TextView Username;
        ImageView messageButton;



        public UserMessagesViewHolder(View itemView) {
            super(itemView);

            Username = itemView.findViewById(R.id.userMessages_username);
            messageButton = itemView.findViewById(R.id.messageButton);


        }
    }

    private void SendUserToPostActivity() {

        Intent addNewPostIntent = new Intent(MainActivity.this,PostActivity.class);
        startActivity(addNewPostIntent);

    }

    @Override
    protected void onStart() {
        super.onStart();

        PopulateAll();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser == null){

            SendUserToLogin();

        }else{

            CheckUserExistence();



            isOnlineStatus("online");

        }
    }



    private void CheckUserExistence() {

        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(currentUserId)){

                    SendUserToSetupActivity();

                }


            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SendUserToSetupActivity() {

        Intent setupIntent = new Intent( MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();

    }

    private void SendUserToLogin() {

        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item)){

            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {

        switch (item.getItemId()){


            case R.id.nav_sekusalnaEksplatacija:
                currentCategory = "Сексуална Експлатација";
                PopulateAll();
                drawerLayout.closeDrawers();
                break;

            case R.id.nav_trudovaEksplatacija:
                currentCategory = "Трудова Експлатација";
                PopulateAll();
                drawerLayout.closeDrawers();
                break;

            case R.id.nav_prinudniBrakovi:
                currentCategory = "Принудни Бракови";
                PopulateAll();
                drawerLayout.closeDrawers();
                break;

            case R.id.nav_transplatacijaNaOrgani:
                currentCategory = "Трансплатација на Органи";
                PopulateAll();
                drawerLayout.closeDrawers();
                break;

            case R.id.nav_posvojuvanjeDeca:
                currentCategory = "Посвојување Деца";
               PopulateAll();
                drawerLayout.closeDrawers();
                break;

            case R.id.nav_userMessages:
                PopulateUserMessages();
                drawerLayout.closeDrawers();
                break;


            case R.id.nav_logout:

                firebaseAuth.signOut();

                SendUserToLogin();

                break;

        }

    }

    private void PopulateAll() {

        DatabaseReference currentCategoryRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(currentCategory);

        Query limited = currentCategoryRef.limitToLast(i * TOTAL_ITEMS_TO_LOAD);




        FirebaseRecyclerOptions<Posts> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(limited,Posts.class).build();

        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final PostsViewHolder holder, int position, @NonNull Posts model) {

                final String postKey = getRef(position).getKey();
                final String postCategoryEdit = model.getCategory();

                holder.Username.setText(model.getFirstName() +" "+ model.getLastName());
                holder.Date.setText(model.getDate() + " ");
                holder.Time.setText(model.getTime());
                holder.Content.setText(model.getContent());
                holder.PostName.setText(model.getPostName());
                holder.ekstra.setText(model.getEkstra());








                getSupportActionBar().setTitle(model.getCategory());
                final String postContents = model.getContent();
                final String postCategory = model.getCategory();
                final String userID = model.getUid();
                final String FirstName = model.getFirstName();
                final String LastName = model.getLastName();
                final String helper = model.getHasHelper();




               /** holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent clickPostIntent = new Intent(MainActivity.this,ClickPostActivity.class);

                        clickPostIntent.putExtra("PostKey",postKey);
                        clickPostIntent.putExtra("PostCategoryEdit",postCategoryEdit);


                        startActivity(clickPostIntent);

                    }
                });*/
                /**   holder.CommentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        Intent commentButtonIntent = new Intent(MainActivity.this,CommentsActivity.class);
                        commentButtonIntent.putExtra("PostKey",postKey);
                        commentButtonIntent.putExtra("PostContent",postContents);
                        commentButtonIntent.putExtra("PostCategory",postCategory);
                        startActivity(commentButtonIntent);

                    }
                });
                 */


                holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        userRef.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String FirstName = dataSnapshot.child("FirstName").getValue().toString();
                                String LastName = dataSnapshot.child("LastName").getValue().toString();
                                 receverFullName = FirstName + " " + LastName;




                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        Intent chatIntent = new Intent(MainActivity.this,ChatActivity.class);
                        chatIntent.putExtra("user_id",userID);
                        chatIntent.putExtra("ReceverFirstName",FirstName);
                        chatIntent.putExtra("ReceverLastName",LastName);
                        chatIntent.putExtra("SenderFirstName", CurrentUserFirstName);
                        chatIntent.putExtra("SenderLastName", CurrentUserLastName);


                        startActivity(chatIntent);

                    }
                });




            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_posts_layout,parent,false);
                PostsViewHolder postsViewHolder = new PostsViewHolder(view);
                return postsViewHolder;
            }



        };


        recyclerViewAll.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        mainSwipeRefreshLayout.setRefreshing(false);



    }


   public void PopulateUserMessages(){


       DatabaseReference userMessagesRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId);





       FirebaseRecyclerOptions<UserMessages> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<UserMessages>().setQuery(userMessagesRef,UserMessages.class).build();

       FirebaseRecyclerAdapter<UserMessages, UserMessagesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserMessages, UserMessagesViewHolder>(firebaseRecyclerOptions) {
           @Override
           protected void onBindViewHolder(@NonNull final UserMessagesViewHolder holder, int position, @NonNull UserMessages model) {


               holder.Username.setText(model.getFirstName() + " " + model.getLastName());

               final String userID = model.getUid();
               final String FirstName = model.getFirstName();
               final String LastName = model.getLastName();

               holder.messageButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {


                       userRef.child(userID).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                               String FirstName = dataSnapshot.child("FirstName").getValue().toString();
                               String LastName = dataSnapshot.child("LastName").getValue().toString();
                               receverFullName = FirstName + " " + LastName;




                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });

                       Intent chatIntent = new Intent(MainActivity.this,ChatActivity.class);
                       chatIntent.putExtra("user_id",userID);
                       chatIntent.putExtra("ReceverFirstName",FirstName);
                       chatIntent.putExtra("ReceverLastName",LastName);
                       chatIntent.putExtra("SenderFirstName", CurrentUserFirstName);
                       chatIntent.putExtra("SenderLastName", CurrentUserLastName);


                       startActivity(chatIntent);

                   }
               });



           }

           @NonNull
           @Override
           public UserMessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_messages_occupants,parent,false);
               UserMessagesViewHolder userMessagesViewHolder = new UserMessagesViewHolder(view);
               return userMessagesViewHolder;
           }
       };



       recyclerViewAll.setAdapter(firebaseRecyclerAdapter);
       firebaseRecyclerAdapter.startListening();

       mainSwipeRefreshLayout.setRefreshing(false);



   }

    private void isOnlineStatus(String status){

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isOnlineStatus", status);
        dbRef.updateChildren(hashMap);

    }

    @Override
    protected void onPause() {
        super.onPause();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
        final String saveCurrentTime = currentTime.format(calForTime.getTime());

        isOnlineStatus(saveCurrentDate + " " + saveCurrentTime);
    }

    @Override
    protected void onResume() {
        super.onResume();

        isOnlineStatus("online");

    }
}
