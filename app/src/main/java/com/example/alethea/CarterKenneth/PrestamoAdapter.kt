package com.example.alethea.CarterKenneth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alethea.R

data class PrestamoUsuario(
    val id: Int,
    val libroId: Int,
    val titulo: String,
    val autor: String,
    val fechaPrestamo: String,
    val fechaDevolucion: String?,
    val estado: String
)

class PrestamoAdapter(
    private val prestamos: List<PrestamoUsuario>
) : RecyclerView.Adapter<PrestamoAdapter.PrestamoViewHolder>() {

    class PrestamoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvTituloLibro)
        val tvAutor: TextView = view.findViewById(R.id.tvAutor)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val tvFecha1: TextView = view.findViewById(R.id.tvFecha1)
        val tvFecha2: TextView = view.findViewById(R.id.tvFecha2)
        val lblFecha1: TextView = view.findViewById(R.id.lblFecha1)
        val lblFecha2: TextView = view.findViewById(R.id.lblFecha2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): PrestamoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_prestamo_usuario, parent, false)
        return PrestamoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrestamoViewHolder, position: Int) {
        val p = prestamos[position]
        holder.tvTitulo.text = p.titulo
        holder.tvAutor.text = p.autor

        when (p.estado) {
            "Aceptada" -> {
                holder.tvEstado.text = "Aceptada"
                holder.tvEstado.setBackgroundResource(R.drawable.badge_aceptada)
                holder.tvEstado.setTextColor(android.graphics.Color.WHITE)
            }
            "Rechazado" -> {
                holder.tvEstado.text = "Rechazado"
                holder.tvEstado.setBackgroundResource(R.drawable.badge_rechazado)
                holder.tvEstado.setTextColor(android.graphics.Color.WHITE)
            }
            else -> {
                holder.tvEstado.text = "Pendiente"
                holder.tvEstado.setBackgroundResource(R.drawable.badge_sinstock)
                holder.tvEstado.setTextColor(android.graphics.Color.WHITE)
            }
        }

        holder.lblFecha1.text = "Fecha de solicitud:"
        holder.tvFecha1.text = p.fechaPrestamo
        if (p.fechaDevolucion != null) {
            holder.lblFecha2.text = "Fecha de devolución:"
            holder.tvFecha2.text = p.fechaDevolucion
        } else {
            holder.lblFecha2.visibility = View.GONE
            holder.tvFecha2.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = prestamos.size
}
