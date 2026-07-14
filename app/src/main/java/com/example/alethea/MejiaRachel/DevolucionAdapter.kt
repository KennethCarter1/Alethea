package com.example.alethea.MejiaRachel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.alethea.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Devolucion(
    val prestamoId: Int,
    val usuarioNombre: String,
    val libroNombre: String,
    val fechaPrestamo: String,
    val fechaDevolucion: String?,
    val fechaEntrega: String?,
    val estado: String
)

class DevolucionAdapter(
    private val devoluciones: List<Devolucion>,
    private val onDevolver: (Devolucion) -> Unit
) : RecyclerView.Adapter<DevolucionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsuario: TextView = view.findViewById(R.id.tvUsuario)
        val tvLibro: TextView = view.findViewById(R.id.tvLibro)
        val tvFechaPrestamo: TextView = view.findViewById(R.id.tvFechaPrestamo)
        val tvFechaLimite: TextView = view.findViewById(R.id.tvFechaLimite)
        val filaFechaEntrega: View = view.findViewById(R.id.filaFechaEntrega)
        val tvFechaEntrega: TextView = view.findViewById(R.id.tvFechaEntrega)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val btnMarcarDevuelto: View = view.findViewById(R.id.btnMarcarDevuelto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_devolucion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val d = devoluciones[position]
        holder.tvUsuario.text = d.usuarioNombre
        holder.tvLibro.text = d.libroNombre
        holder.tvFechaPrestamo.text = d.fechaPrestamo
        holder.tvFechaLimite.text = d.fechaDevolucion ?: "-"
        holder.filaFechaEntrega.visibility = if (d.fechaEntrega.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.tvFechaEntrega.text = d.fechaEntrega ?: "-"
        holder.btnMarcarDevuelto.visibility = if (d.estado == "Aceptada") View.VISIBLE else View.GONE
        holder.btnMarcarDevuelto.setOnClickListener { onDevolver(d) }

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val hoy = sdf.format(Date())
        val estaAtrasado = d.estado == "Aceptada" && d.fechaDevolucion != null && d.fechaDevolucion < hoy

        if (estaAtrasado) {
            holder.tvEstado.text = "Atrasado"
            holder.tvEstado.setBackgroundResource(R.drawable.badge_atrasado)
            holder.tvEstado.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.rachel_texto_estado_atrasado)
            )
        } else {
            holder.tvEstado.text = d.estado
            when (d.estado) {
                "Aceptada", "Devuelto" -> {
                    holder.tvEstado.setBackgroundResource(R.drawable.badge_aceptada)
                    holder.tvEstado.setTextColor(
                        ContextCompat.getColor(holder.itemView.context, R.color.rachel_texto_estado_claro)
                    )
                }
                "Rechazado" -> {
                    holder.tvEstado.setBackgroundResource(R.drawable.badge_rechazado)
                    holder.tvEstado.setTextColor(
                        ContextCompat.getColor(holder.itemView.context, R.color.rachel_texto_estado_claro)
                    )
                }
                else -> {
                    holder.tvEstado.setBackgroundResource(R.drawable.badge_pendiente)
                    holder.tvEstado.setTextColor(
                        ContextCompat.getColor(holder.itemView.context, R.color.rachel_texto_estado_claro)
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int = devoluciones.size
}
