package com.example.myapplicationarturocashfaster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var tvEmptyOrders: TextView
    private lateinit var btnBackToStore: Button
    private lateinit var ordersAdapter: OrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        initViews()
        setupRecyclerView()
        checkEmptyOrders()
    }

    private fun initViews() {
        rvOrders = findViewById(R.id.rvOrders)
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders)
        btnBackToStore = findViewById(R.id.btnBackToStore)

        btnBackToStore.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        val orders = OrderManager.getOrders()

        ordersAdapter = OrdersAdapter(orders,
            onOrderClick = { order ->
                showOrderDetails(order)
            }
        )

        rvOrders.layoutManager = LinearLayoutManager(this)
        rvOrders.adapter = ordersAdapter
    }

    private fun checkEmptyOrders() {
        val orders = OrderManager.getOrders()
        if (orders.isEmpty()) {
            tvEmptyOrders.visibility = View.VISIBLE
            rvOrders.visibility = View.GONE
        } else {
            tvEmptyOrders.visibility = View.GONE
            rvOrders.visibility = View.VISIBLE
        }
    }

    private fun showOrderDetails(order: Order) {
        val intent = Intent(this, OrderDetailActivity::class.java).apply {
            putExtra("ORDER_ID", order.id)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        checkEmptyOrders()
        ordersAdapter.notifyDataSetChanged()
    }
}