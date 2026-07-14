package com.example.alethea.MejiaRachel

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alethea.R
import java.io.File

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
        val tvStock: TextView = view.findViewById(R.id.tvStockLibro)
        val ivPortada: ImageView = view.findViewById(R.id.ivPortada)
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
        holder.tvStock.text = holder.itemView.context.getString(
            R.string.item_libro_stock_formato,
            libro.stock,
            libro.disponibles
        )

        if (libro.rutaImagen.isNotEmpty()) {
            val archivo = File(holder.itemView.context.filesDir, "images/${libro.rutaImagen}")
            if (archivo.exists()) {
                val bitmap = BitmapFactory.decodeFile(archivo.absolutePath)
                holder.ivPortada.setImageBitmap(bitmap)
            }
        }

        holder.itemView.findViewById<View>(R.id.btnEditarItem).setOnClickListener {
            onEditar?.invoke(libro)
        }
        holder.itemView.findViewById<View>(R.id.btnEliminarItem).setOnClickListener {
            onEliminar?.invoke(libro)
        }
    }

    override fun getItemCount(): Int = listaLibros.size
}
