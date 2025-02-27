package com.example.pis_entrega1.Activities;

import android.annotation.SuppressLint;
import com.example.pis_entrega1.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pis_entrega1.Model.MainActivityViewModel;
import com.example.pis_entrega1.Model.MyAdapter;
import com.example.pis_entrega1.Model.MyAdapterDelete;
import com.example.pis_entrega1.Note.Notes;
import com.example.pis_entrega1.Note.Photo;
import com.example.pis_entrega1.Note.Recording;
import com.example.pis_entrega1.Note.Text;
import com.example.pis_entrega1.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

/**
 * Class hosting the home page of the App
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;

    private MyAdapter mAdapter;

    public Context parentContext;
    private MainActivityViewModel viewModel;
    boolean clicked;
    FloatingActionButton fabText, fabAudio, fabPhoto;
    ExtendedFloatingActionButton addNote;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    NavigationView navigationView;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(viewModel.getListNotes().getValue(), fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };


    /**
     * Method to set all the widgets of the layout
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentContext = this.getBaseContext();

        setContentView(R.layout.activity_main);

        setFloatingActionButtons();

        setMenu();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    /**
     * Method to set on the Floating Button
     */
    private void setFloatingActionButtons () {
        addNote = findViewById(R.id.add_fab);

        fabText = findViewById(R.id.TextButton);
        fabAudio = findViewById(R.id.AudioButton);
        fabPhoto = findViewById(R.id.CameraButton);


        fabText.setVisibility(View.GONE);
        fabAudio.setVisibility(View.GONE);
        fabPhoto.setVisibility(View.GONE);

        clicked = false;

        addNote.shrink();

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!clicked) {

                    fabText.show();
                    fabAudio.show();
                    fabPhoto.show();

                    addNote.extend();

                    clicked = true;
                } else {
                    fabText.hide();
                    fabAudio.hide();
                    fabPhoto.hide();

                    addNote.shrink();

                    clicked = false;
                }
            }
        });

        fabText.setOnClickListener(this);
        fabAudio.setOnClickListener(this);
        fabPhoto.setOnClickListener(this);
    }

    /**
     * Method to set the menu and set the recycler view
     */
    private void setMenu () {
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        addNote.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);

        viewModel = new MainActivityViewModel();

        addNote.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);

        setLiveDataObservers();
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.bringToFront();
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_delete:
                            toDeleteMode();
                            break;
                        case R.id.nav_logout:
                            SharedPreferences preferences = getSharedPreferences("checkbox", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("checkbox_clicked", "true");
                            editor.putString("saved_username", "");
                            editor.putString("saved_password", "");

                            editor.commit();

                            finish();
                            break;
                        default:
                    }
                    return true;
                }
            });
        }
    }

    /**
     * Method to go to delete view and delete notes from the collection
     */
    private void toDeleteMode() {
        setContentView(R.layout.activity_delete_note);
        Button button_cancel = findViewById(R.id.cancelButton);
        Button button_delete = findViewById(R.id.deleteButton);
        RecyclerView recyclerViewDelete = findViewById(R.id.recyclerViewDelete);
        recyclerViewDelete.setLayoutManager(new LinearLayoutManager(this));
        MyAdapterDelete myAdapterDelete = new MyAdapterDelete(this, viewModel.getListNotes().getValue());
        recyclerViewDelete.setAdapter(myAdapterDelete);

        myAdapterDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myAdapterDelete.addPosition(recyclerViewDelete.getChildAdapterPosition(view));
            }
        });

        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myAdapterDelete.getSelected().size() > 0) {
                    for (int i = 0; i < myAdapterDelete.getSelected().size(); i++) {
                        viewModel.deleteNote(myAdapterDelete.getSelected().get(i));
                    }
                }
                fromDeleteMode();
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDeleteMode();
            }
        });

    }

    /**
     * Method to return to Home page from the delete view
     */
    private void fromDeleteMode() {
        System.out.println("fromDeleteMode");
        setContentView(R.layout.activity_main);
        this.setTable();
        setFloatingActionButtons();
        setMenu();
    }

    /**
     * Method to set the ArrayList of notes observable, and set the recycler view when data changed
     */
    public void setLiveDataObservers() {

        final Observer<ArrayList<Notes>> observer = new Observer<ArrayList<Notes>>() {
            @Override
            public void onChanged(ArrayList<Notes> ac) {
                MyAdapter newAdapter = new MyAdapter(parentContext, ac);
                newAdapter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewModel.getNotesById(mRecyclerView.getChildAdapterPosition(v)) instanceof Text) {
                            passDataText((Text) viewModel.getNotesById(mRecyclerView.getChildAdapterPosition(v)), mRecyclerView.getChildAdapterPosition(v));
                        } else if (viewModel.getNotesById(mRecyclerView.getChildAdapterPosition(v)) instanceof Recording) {
                            passDataAudio((Recording) viewModel.getNotesById(mRecyclerView.getChildAdapterPosition(v)), mRecyclerView.getChildAdapterPosition(v));
                        } else {
                            passDataPhoto((Photo) viewModel.getNotesById(mRecyclerView.getChildAdapterPosition(v)), mRecyclerView.getChildAdapterPosition(v));
                        }
                        Toast.makeText(getApplicationContext(), "Selección: " + viewModel.getNotesById(mRecyclerView.getChildAdapterPosition(v)).getName(), Toast.LENGTH_SHORT).show();
                    }
                });
                mRecyclerView.swapAdapter(newAdapter, false);
                newAdapter.notifyDataSetChanged();
            }
        };

        final Observer<String> observerToast = new Observer<String>() {
            @Override
            public void onChanged(String t) {
                Toast.makeText(parentContext, t, Toast.LENGTH_SHORT).show();
            }
        };
        viewModel.getListNotes().observe(this, observer);
        viewModel.getToast().observe(this, observerToast);
    }

    /**
     * Method to take all the possible Results of Activities
     * If(Result code == RESULT_OK): we come from save of one of the notes
     * else if (resultCode == 5): we come from Text Note to modify any attribute of the note
     * else if (resultCode == 2): we come from Audio Note to modify any attribute of the note
     * else if (resultCode == 3): we come from Photo Note to modify any attribute of the note
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (intent.getStringExtra("title") != null) {
                    Text text_temp = new Text(intent.getStringExtra("date"), intent.getStringExtra("title"), intent.getStringExtra("text"));
                    this.viewModel.addTextNote(text_temp);
                } else if (intent.getStringExtra("title_audio_main") != null) {
                    Recording recordingTemp = new Recording(intent.getStringExtra("date_audio_main"), intent.getStringExtra("title_audio_main"), intent.getStringExtra("Adress_main"));
                    this.viewModel.addAudioNote(recordingTemp);
                } else if (intent.getStringExtra("title_photo_main") != null) {
                    Photo photoTemp = new Photo(intent.getStringExtra("date_photo_main"), intent.getStringExtra("title_photo_main"), intent.getStringExtra("Adress_main"));
                    this.viewModel.addPhotoNote(photoTemp);
                }
            } else if (resultCode == 5) {
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
                String date = df.format(Calendar.getInstance().getTime());
                Text text_temp = new Text(intent.getStringExtra("title"), intent.getStringExtra("text"), date, intent.getStringExtra("id"));
                Log.e("main", text_temp.getID());
                this.viewModel.modifyTextNote(text_temp, intent.getIntExtra("positionText", -1));
                this.setTable();

            } else if (resultCode == 2) {
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
                String date = df.format(Calendar.getInstance().getTime());
                Recording audio_temp = new Recording(intent.getStringExtra("title_audio"), intent.getStringExtra("Adress"), intent.getStringExtra("id"), date, intent.getStringExtra("url"));
                this.viewModel.modifyAudioNote(audio_temp, intent.getIntExtra("positionAudio", -1));
                this.setTable();
            } else if (resultCode == 3) {
                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
                String date = df.format(Calendar.getInstance().getTime());
                Photo photo_temp = new Photo(intent.getStringExtra("title_photo"), intent.getStringExtra("path"), intent.getStringExtra("id"), date, intent.getStringExtra("url"));
                this.viewModel.modifyPhotoNote(photo_temp, intent.getIntExtra("positionPhoto", -1));
                this.setTable();
            }
        }
    }

    /**
     * Methd to go to Text Note (new Note)
     */
    public void goToTextNote() {
        Intent i = new Intent(this, TextNote.class);
        startActivityForResult(i, 1);
    }

    /**
     * Method to go to Audio Note (new Note)
     */
    public void goTOAudioNote() {
        Intent i = new Intent(this, AudioNote.class);
        startActivityForResult(i, 1);
    }

    /**
     * Method to go to Photo Note (new Note)
     */
    public void goToCameraNote() {
        Intent i = new Intent(this, PhotoNote.class);
        startActivityForResult(i, 1);
    }

    /**
     * Method to control the on click listeners of the view
     * If we press text button, we go to Text Note
     * If we press Audio button, we go to Audio Note
     * If we press Photo button, we go to Photo Note
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (R.id.TextButton == v.getId()) {
            goToTextNote();
            fabText.hide();
            fabAudio.hide();
            fabPhoto.hide();

            addNote.shrink();

            clicked = false;
        }
        if (R.id.AudioButton == v.getId()) {
            goTOAudioNote();
            fabText.hide();
            fabAudio.hide();
            fabPhoto.hide();

            addNote.shrink();

            clicked = false;
        }
        if (R.id.CameraButton == v.getId()) {
            goToCameraNote();
            fabText.hide();
            fabAudio.hide();
            fabPhoto.hide();

            addNote.shrink();

            clicked = false;
        }
    }

    /**
     * Method to set manually the list of notes on recycler view
     */
    void setTable() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyAdapter(this, viewModel.getListNotes().getValue());
        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getNotesById(recyclerView.getChildAdapterPosition(v)) instanceof Text) {
                    passDataText((Text) viewModel.getNotesById(recyclerView.getChildAdapterPosition(v)), recyclerView.getChildAdapterPosition(v));
                } else if (viewModel.getNotesById(recyclerView.getChildAdapterPosition(v)) instanceof Recording) {
                    passDataAudio((Recording) viewModel.getNotesById(recyclerView.getChildAdapterPosition(v)), recyclerView.getChildAdapterPosition(v));
                } else {
                    passDataPhoto((Photo) viewModel.getNotesById(recyclerView.getChildAdapterPosition(v)), recyclerView.getChildAdapterPosition(v));
                }
                Toast.makeText(getApplicationContext(), "Selección: " + viewModel.getNotesById(recyclerView.getChildAdapterPosition(v)).getName(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Method to open a Text Note
     * @param text Text to open
     * @param position Position of the note selected
     */
    void passDataText(Text text, int position) {
        fabText.hide();
        fabAudio.hide();
        fabPhoto.hide();

        addNote.shrink();

        clicked = false;
        Intent i = new Intent(this, TextNote.class);
        i.putExtra("newTitleText", text.getName());
        i.putExtra("newTextText", text.getText());
        i.putExtra("id", text.getID());
        i.putExtra("positionText", position);
        startActivityForResult(i, 1);
    }

    /**
     * Method to open a Audio Note
     * @param recording Audio to open
     * @param position Position of the note selected
     */
    void passDataAudio(Recording recording, int position) {
        fabText.hide();
        fabAudio.hide();
        fabPhoto.hide();

        addNote.shrink();

        clicked = false;
        Intent n1 = new Intent(this, AudioRecorded.class);
        n1.putExtra("newTitleAudio", recording.getName());
        n1.putExtra("Adress", recording.getAddress());
        n1.putExtra("id", recording.getID());
        n1.putExtra("url", recording.getUrl());
        n1.putExtra("positionAudio", position);
        startActivityForResult(n1, 1);
    }

    /**
     * Method to open Photo Note
     * @param photo Photo to open
     * @param position Position of the note selected
     */
    void passDataPhoto(Photo photo, int position) {
        fabText.hide();
        fabAudio.hide();
        fabPhoto.hide();

        addNote.shrink();

        clicked = false;
        Intent n = new Intent(this, PhotoTaken.class);
        n.putExtra("newTitlePhoto", photo.getName());
        n.putExtra("path", photo.getAddress());
        n.putExtra("id", photo.getID());
        n.putExtra("url", photo.getUrl());
        n.putExtra("positionPhoto", position);
        startActivityForResult(n, 1);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return false;
    }
}
