package com.example.nftportfolio.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nftportfolio.MainActivity;
import com.example.nftportfolio.R;
import com.example.nftportfolio.databinding.FragmentHomeBinding;
import com.example.nftportfolio.model.NFT;
import com.example.nftportfolio.model.NFTRepository;
import com.example.nftportfolio.ui.RecyclerView.NFTAdapter;
import com.example.nftportfolio.ui.collection.CollectionFragment;
import com.example.nftportfolio.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Observer;

public class HomeFragment extends Fragment {
    private FirebaseAuth mAuth;
    HomeViewModel viewModel;

    RecyclerView nftList;

    private FragmentHomeBinding binding;
    private TextView textView, walletWorth;
    private NFTRepository repo;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(HomeViewModelImpl.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        repo = NFTRepository.getInstance();

        bindings();
        setText();

        return root;
    }

    private void setText() {
        if(mAuth.getCurrentUser() != null)
            textView.setText("Hej "+mAuth.getCurrentUser().getEmail());
        else
            textView.setText("Ikke logget ind");

        //Liste
        nftList = binding.getRoot().findViewById(R.id.rv);
        nftList.hasFixedSize();
        nftList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        repo.getListNFTs().observe(getViewLifecycleOwner(), nfts -> {
            NFTAdapter b = new NFTAdapter(nfts);
            nftList.setAdapter(b);

            b.setOnClickListner(nft -> {
                System.out.println(nft.getName());
//            Intent i = new Intent(getActivity(), CollectionFragment.class);
//            i.putExtra("collection", (Parcelable) nft.getCollection());
//            startActivity(i);
            });
        });

        repo.getWalletWorth().observe(getViewLifecycleOwner(), wallet -> {
            walletWorth.setText(""+wallet);
        });

    }

    private void bindings() {
        textView = binding.textHome;
        walletWorth = binding.walletWorth;

        Button b = binding.refreshBTN;
        b.setOnClickListener(v -> {
            viewModel.refresh();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}