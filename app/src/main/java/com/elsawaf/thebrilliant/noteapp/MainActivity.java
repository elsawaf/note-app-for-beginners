package com.elsawaf.thebrilliant.noteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private List<Note> notes;
    private NotesAdapter adapter;
    private EditText searchET;

    public static final int LINEAR_FLAG = 0;
    public static final int GRID_FLAG = 1;
    public static final int STAGGERED_FLAG = 2;
    public static final String PREF_LAYOUT_FLAG = "pref_layout_key";

    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private LinearLayout emptyLayout;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchET = (EditText) findViewById(R.id.search_edit_text);
        addAutomaticSearchToEditText();

        emptyLayout = (LinearLayout) findViewById(R.id.layout_empty_data);
        recyclerView = (RecyclerView) findViewById(R.id.main_list);

        /*هنا بجيب الليسته من الداتا بيز*/
        notes = Note.listAll(Note.class);

        adapter = new NotesAdapter(this, notes);
        recyclerView.setAdapter(adapter);

        // فيه احتمال ان الداتا بيز تكون فاضيه عشان كده بعرض رسالة لليوزر لو الليسته فاضية
        if (notes == null || notes.isEmpty()) {
            toggleEmptyView();
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int layoutFlag = sharedPreferences.getInt(PREF_LAYOUT_FLAG, 0);
        applyLayoutManagerToRecyclerView(layoutFlag);

        addDeleteOnSwipe();
    }

    private void addAutomaticSearchToEditText() {
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Toast.makeText(MainActivity.this, s.toString(), Toast.LENGTH_SHORT).show();
                notes = Note.findWithQuery(Note.class,
                        "Select * from Note where title like '%" + s.toString() + "%'");
                if (!notes.isEmpty()){
                    adapter.setNoteList(notes);
                    adapter.notifyDataSetChanged();
                } else {
                    if (mToast == null) {
                        mToast = Toast.makeText(MainActivity.this, getString(R.string.title_no_search_result)
                                + s.toString(), Toast.LENGTH_SHORT);
                    }
                    mToast.setText(getString(R.string.title_no_search_result) + s.toString());
                    mToast.show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void addDeleteOnSwipe() {
        // هنا بنضيف ال swipe للريسكلر فيو وعشان اضيفه لازم الاول اعرف كلاس عباره عن callback
        // الكلاس دا فيه اتنين ميثود واحده منهم الاندرويد بينده عليها لما اليوزر يعمل swipe ودي اللي هنكتب فيها الكود اللي احنا عايزينه يتنفذ
        // ال constructor بتاع الكلاس دا بياخد مني معلومتين الاولى هاحطها بصفر لان مش ممحتاجها والتانيه هي عباره عن الاتجاه بتاع ال swipe اللي انا عايز اتعامل معاه
        ItemTouchHelper.SimpleCallback simpleCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        Toast.makeText(MainActivity.this, "Swiped", Toast.LENGTH_SHORT).show();

                        // الاكشن اللي انا عايز انفذه هو حذف النوت دي فاول حاجة بجيب ال position بتاعها وبعدين بحذفها من الداتا بيز
                        int position = viewHolder.getAdapterPosition();
                        Note note = notes.get(position);
                        note.delete();

                        // وبعدين بحذفها من الليسته واعمل ريفرش للريسكلر فيو
                        notes.remove(position);
                        adapter.notifyItemRemoved(position);

                        // في حالة ان الليسته فضيت بخفيها واظهر الرسالة
                        if (notes.isEmpty()) {
                            toggleEmptyView();
                        }
                    }
                };

        // الكلاس دا هو اللي بيعرف ان اليوزر عمل swipe وبعد كده بينده على ال callback اللي انا لسه معرفه عشان كده ببصهوله في ال constructor
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        // واخر خطوة بربط الكلاس دا بالريسكلر فيو
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void applyLayoutManagerToRecyclerView(int layoutFlag) {
        RecyclerView.LayoutManager layoutManager;
        switch (layoutFlag) {
            case LINEAR_FLAG:
                layoutManager = new LinearLayoutManager(this);
                break;
            case GRID_FLAG:
                layoutManager = new GridLayoutManager(this, 2);
                break;
            case STAGGERED_FLAG:
                layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                break;
            default:
                layoutManager = new LinearLayoutManager(this);
        }
        recyclerView.setLayoutManager(layoutManager);
    }

    private void toggleEmptyView() {
        if (notes == null || notes.isEmpty()){
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
        else {
            emptyLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // الميثود دي بيتنده عليها لما اليوزر يرجع للاكتفيتي دا بعد ما يضيف ملاحظة جديدة
        // فانا هنا عايز اعمل ريفرش لل UI واجيب اخر ملاحظة اضافة في الداتا بيز واعرضها في الريسكلر فيو
        // فببدا ان بشوف عدد العناصر اللي موجودين في الداتا بيز واقارنهم بعدد العناصر الموجود في الريسكللر فيو
        long newCount = Note.count(Note.class);
        if (newCount > notes.size()) {
            // لو الداتا بيز فيها زيادة بجيب اخر عنصر في الداتا بيز واضيفه لليسته وبعدين اعمل notify لل adapter
            Note note = Note.last(Note.class);
            notes.add(note);
            int position = notes.size() - 1;
            adapter.notifyItemInserted(position);

            // في حالة ان دا اول ملاحظة تم اضافتها هتكون الريسكلر فيو مخفية فلازم اظهرها واخفي الرسالة
            if (newCount == 1) {
                toggleEmptyView();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_new_note_action){
            openAddNoteActivity();
            return true;
        }
        else if (item.getItemId() == R.id.settings_action) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (item.getItemId() == R.id.linear_action) {
            applyLayoutManagerToRecyclerView(LINEAR_FLAG);
            saveUserLayoutChoice(LINEAR_FLAG);
            return true;
        }
        else if (item.getItemId() == R.id.grid_action) {
            applyLayoutManagerToRecyclerView(GRID_FLAG);
            saveUserLayoutChoice(GRID_FLAG);
            return true;
        }
        else if (item.getItemId() == R.id.staggered_action) {
            applyLayoutManagerToRecyclerView(STAGGERED_FLAG);
            saveUserLayoutChoice(STAGGERED_FLAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveUserLayoutChoice(int layoutFlag) {
        // الكود دا بيتنفئ في 3 اماكن عشان كده حطيته في ميثود خاصة بيه وبندها عليها
        // فبالتالي الكود مكتوب في مكان واحد لو احتجت اعدل عليه هاعدل عليه مره واحده
        // دي فايدة الميثود عود نفسك عليها حتى لو هاتعمل كود بسيط المهم مايبقاش فيه تكرار عندك في البروجكت
        sharedPreferences.edit().putInt(PREF_LAYOUT_FLAG, layoutFlag).apply();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    public void openAddNoteActivity() {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivity(intent);
    }

    public void addNoteClick(View view) {
        openAddNoteActivity();
    }
}
