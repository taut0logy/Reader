package com.advancedprogramming.jollypdf;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class JumpToPageFragment extends AppCompatDialogFragment {

    private EditText jumpInput;
    private Button jumpButton;
    private TextView totalPage;
    private int pagecnt,curpage;

    public interface JumpToPageListener {
        void onJumpToPage(int pageNumber);
    }

    public JumpToPageFragment(int n,int m){this.pagecnt=n;curpage=m;}

    private JumpToPageListener listener;

    public void setJumpToPageListener(JumpToPageListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jump_to_page, container, false);

        jumpInput = view.findViewById(R.id.jumpInp);
        jumpButton = view.findViewById(R.id.jumpbtn);
        totalPage=view.findViewById(R.id.totalPage);
        jumpInput.setHint(String.valueOf(curpage));
        totalPage.setText("/"+String.valueOf(pagecnt));

        jumpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToPage();
            }
        });

        return view;
    }

    private void jumpToPage() {
        if (listener != null) {
            String pageNumberStr = jumpInput.getText().toString();
            if (!pageNumberStr.isEmpty()) {
                int pageNumber = Integer.parseInt(pageNumberStr);
                listener.onJumpToPage(pageNumber);
                dismiss(); // Close the dialog after jumping to the page
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Jump to Page");
        return dialog;
    }
}
