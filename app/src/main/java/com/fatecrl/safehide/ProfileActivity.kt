package com.fatecrl.safehide

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fatecrl.safehide.databinding.ProfilePageBinding
import com.fatecrl.safehide.services.FirebaseService.auth
import com.fatecrl.safehide.services.FirebaseService.database

class ProfileActivity : AppCompatActivity() {

    // Declaração das variáveis de entrada de texto e botões
    private lateinit var binding: ProfilePageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obter o ID do usuário atualmente autenticado
        val userId = auth.currentUser?.uid

        binding.apply {

            userId?.let {         // Se userId não for nulo, executa o bloco let
                loadUserProfile(it)      // Passa o valor de userId (referenciado como it) para loadUserProfile
            } ?: run {// it é uma maneira conveniente de se referir ao único argumento de uma lambda quando o contexto é claro
                displayUserNotFound()   // Se userId for nulo, executa o bloco run e a função displayUserNotFound é chamada
            }

            setupListeners()
        }
    }

    private fun loadUserProfile(userId: String){
        // Recuperar os dados do perfil do usuário do Cloud Firestore
        database.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val name = document.getString("username")
                    val email = document.getString("email")
                    val keySecret = document.getString("secretPassword")

                    // Exibir os dados do perfil do usuário na interface do usuário
                    binding.apply {
                        userNameView.text = name
                        nameView.text = name
                        nameView.text = email
                        secretPasswordView.text = keySecret
                    }
                } else {
                    displayUserNotFound()
                }
            }
    }

    private fun displayUserNotFound() {
        binding.apply {
            userNameView.text = "Dados não encontrados"
            nameView.text = "Dados não encontrados"
            emailView.text = "Dados não encontrados"
            secretPasswordView.text = "Dados não encontrados"
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnBack.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, HomeActivity::class.java))
            }

            btnEdit.setOnClickListener {
                startActivity(Intent(this@ProfileActivity, EditProfileActivity::class.java))
            }

            btnLogout.setOnClickListener {
                // Deslogar o usuário do FirebaseAuth
                auth.signOut()

                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))

                // Limpar a pilha de atividades e evitar que o usuário volte para a tela de perfil
                finishAffinity()
            }
        }
    }
}