package net.app.civilix;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class ProjectAdapter extends ArrayAdapter<Project> {

    Context mCtx;
    int listLayoutRes;
    List<Project> projectList;
    SQLiteDatabase mDatabase;
    public int id;

    public ProjectAdapter(Context mCtx, int listLayoutRes, List<Project> projectList, SQLiteDatabase mDatabase) {
        super(mCtx, listLayoutRes, projectList);

        this.mCtx = mCtx;
        this.listLayoutRes = listLayoutRes;
        this.projectList = projectList;
        this.mDatabase = mDatabase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(listLayoutRes, null);

        final Project project = projectList.get(position);


        TextView textViewName = view.findViewById(R.id.textViewName);

        TextView textViewJoiningDate = view.findViewById(R.id.textViewJoiningDate);


        textViewName.setText(project.getName());
        textViewJoiningDate.setText(project.getJoiningDate());


        Button buttonDelete = view.findViewById(R.id.buttonDeleteProject);
        Button buttonEdit = view.findViewById(R.id.buttonEditProject);
        LinearLayout projectsLL=view.findViewById(R.id.projectsLL);

        projectsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
//                 ProjectActions.id=project.getId();
//                 id=project.getId();

//                 String s="SELECT name FROM projects WHERE id = ?";
//                 mDatabase.execSQL(s,new Integer[]{project.getId()});

            //mCtx.startActivity(new Intent(mCtx,Note_editor.class));
                Intent note=new Intent(mCtx,Note_editor.class);
                note.putExtra("note_name",project.getName());
                mCtx.startActivity(note);
            }
        });

        //adding a clicklistener to button
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProject(project);
            }
        });

        //the delete operation
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sql = "DELETE FROM projects WHERE id = ?";
                        mDatabase.execSQL(sql, new Integer[]{project.getId()});
                        dropTables(project);
                        reloadProjectsFromDatabase();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return view;
    }

    private void dropTables(Project project) {
        String sql="DROP TABLE "+project.getName()+"_inflow";
        mDatabase.execSQL(sql);
        sql="DROP TABLE "+project.getName()+"_outflow";
        mDatabase.execSQL(sql);
    }

//
    private void updateProject(final Project project) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.dialog_update_project, null);
        builder.setView(view);


        final EditText editTextName = view.findViewById(R.id.editTextName);

        final String oldname=project.getName();

        editTextName.setText(project.getName());


        final AlertDialog dialog = builder.create();
        dialog.show();

        view.findViewById(R.id.buttonUpdateProject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();

                if (name.isEmpty()) {
                    editTextName.setError("Note Name can't be blank");
                    editTextName.requestFocus();
                    return;
                }

                String sql = "UPDATE projects \n" +
                        "SET name = ? \n" +
                        "WHERE id = ?;\n";

                mDatabase.execSQL(sql, new String[]{name, String.valueOf(project.getId())});
                if(!(name.equals(oldname))) {
                    sql = "ALTER TABLE " + oldname + "_inflow" + " " + "RENAME TO " + name + "_inflow";
                    mDatabase.execSQL(sql);
                    sql = "ALTER TABLE " + oldname + "_outflow" + " " + "RENAME TO " + name + "_outflow";
                    mDatabase.execSQL(sql);
                }
                Toast.makeText(mCtx, "Notes Updated", Toast.LENGTH_SHORT).show();
                reloadProjectsFromDatabase();

                dialog.dismiss();
            }
        });
    }

    private void reloadProjectsFromDatabase() {
        Cursor cursorProjects = mDatabase.rawQuery("SELECT * FROM projects", null);
        if (cursorProjects.moveToFirst()) {
            projectList.clear();
            do {
                projectList.add(new Project(
                        cursorProjects.getInt(0),
                        cursorProjects.getString(1),
                        cursorProjects.getString(2)
                ));
            } while (cursorProjects.moveToNext());
        }
        cursorProjects.close();
        notifyDataSetChanged();
    }

}
