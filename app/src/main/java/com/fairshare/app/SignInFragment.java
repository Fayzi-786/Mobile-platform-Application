package com.fairshare.app;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.fairshare.app.databinding.FragmentSignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInFragment extends Fragment {
    private FragmentSignInBinding binding;
    private FirebaseAuth auth;

    public SignInFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        NavController nav = Navigation.findNavController(view);

        binding.btnSignIn.setOnClickListener(v -> {
            String email = String.valueOf(binding.editEmail.getText()).trim();
            String pass  = String.valueOf(binding.editPassword.getText());

            if (TextUtils.isEmpty(email)) { binding.tilEmail.setError("Email is required"); return; }
            else binding.tilEmail.setError(null);
            if (TextUtils.isEmpty(pass))  { binding.tilPassword.setError("Password is required"); return; }
            else binding.tilPassword.setError(null);

            binding.btnSignIn.setEnabled(false);
            auth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                        @Override public void onComplete(@NonNull Task<AuthResult> task) {
                            binding.btnSignIn.setEnabled(true);
                            if (task.isSuccessful()) {
                                nav.navigate(R.id.action_signInFragment_to_homeFragment);
                            } else {
                                Toast.makeText(requireContext(),
                                        "Email or password incorrect.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        binding.linkCreateAccount.setOnClickListener(v ->
                nav.navigate(R.id.action_signInFragment_to_registerFragment));
        binding.linkForgotPassword.setOnClickListener(v ->
                nav.navigate(R.id.action_signInFragment_to_forgotPasswordFragment));
    }

    @Override public void onDestroyView() { super.onDestroyView(); binding = null; }
}
