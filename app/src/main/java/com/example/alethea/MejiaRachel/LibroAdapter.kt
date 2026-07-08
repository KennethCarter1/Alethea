package com.example.alethea.MejiaRachel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alethea.R

class LibroAdapter(
    private val listaLibros: List<Libro>,
    private val onEditar: ((Libro) -> Unit)? = null,
    private val onEliminar: ((Libro) -> Unit)? = null
) : RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {

    class LibroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreLibro)
        val tvAutor: TextView = view.findViewById(R.id.tvAutorLibro)
        val tvCategoria: TextView = view.findViewById(R.id.tvCategoriaLibro)
        val tvAnio: TextView = view.findViewById(R.id.tvAnioLibro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_libro, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = listaLibros[position]
        holder.tvNombre.text = libro.nombre
        holder.tvAutor.text = libro.autor
        holder.tvCategoria.text = libro.categoria
        holder.tvAnio.text = libro.anio

        holder.itemView.findViewById<View>(R.id.btnEditarItem).setOnClickListener {
            onEditar?.invoke(libro)
        }
        holder.itemView.findViewById<View>(R.id.btnEliminarItem).setOnClickListener {
            onEliminar?.invoke(libro)
        }
    }

    override fun getItemCount(): Int = listaLibros.size
}