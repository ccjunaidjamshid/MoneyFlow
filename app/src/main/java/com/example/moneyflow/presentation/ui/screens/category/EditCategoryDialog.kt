package com.example.moneyflow.presentation.ui.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.moneyflow.R
import com.example.moneyflow.data.model.Category
import com.example.moneyflow.data.model.CategoryType
import com.example.moneyflow.presentation.ui.theme.AppColors
import com.example.moneyflow.utils.CategoryColors
import com.example.moneyflow.utils.CategoryIcons

/**
 * Dialog for editing an existing category - Styled to match CreateCategoryDialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    onConfirm: (Category) -> Unit
) {
    var name by remember { mutableStateOf(category.name) }
    var selectedType by remember { mutableStateOf(category.type) }
    var selectedIconName by remember { mutableStateOf(category.iconName) }
    var selectedColor by remember { mutableStateOf(CategoryColors.hexToColor(category.color)) }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 8.dp,
            color = AppColors.Surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Title
                Text(
                    text = "Edit Category",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.Primary
                )
                
                // Name input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Category Name", color = AppColors.TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Primary,
                        focusedLabelColor = AppColors.Primary,
                        cursorColor = AppColors.Primary
                    ),
                    singleLine = true
                )
                
                // Category Type Selection (disabled for default categories)
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Category Type",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.OnSurface
                    )
                    
                    if (category.isDefault) {
                        // Show current type but disabled
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AppColors.Primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "${selectedType.displayName} (Default)",
                                modifier = Modifier.padding(12.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.Primary
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CategoryType.values().forEach { type ->
                                FilterChip(
                                    selected = selectedType == type,
                                    onClick = { 
                                        selectedType = type
                                    },
                                    label = { 
                                        Text(
                                            type.displayName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        ) 
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Icon Selection
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Choose Icon",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.OnSurface
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(CategoryIcons.getAllIcons()) { (iconName, iconRes) ->
                            val isSelected = selectedIconName == iconName
                            val iconColor = if (isSelected) selectedColor else AppColors.TextSecondary
                            
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) 
                                            selectedColor.copy(alpha = 0.15f) 
                                        else 
                                            AppColors.Primary.copy(alpha = 0.05f)
                                    )
                                    .clickable { selectedIconName = iconName },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = iconName,
                                    modifier = Modifier.size(24.dp),
                                    tint = iconColor
                                )
                            }
                        }
                    }
                }
                
                // Color Selection
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Choose Color",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.OnSurface
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(CategoryColors.getDefaultColors()) { color ->
                            val isSelected = selectedColor == color
                            
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { selectedColor = color },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_check),
                                        contentDescription = "Selected",
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppColors.TextSecondary
                        )
                    ) {
                        Text(
                            "Cancel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(
                                    category.copy(
                                        name = name.trim(),
                                        type = selectedType,
                                        iconName = selectedIconName,
                                        color = CategoryColors.colorToHex(selectedColor)
                                    )
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.Primary,
                            contentColor = AppColors.Surface
                        )
                    ) {
                        Text(
                            "Update",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}


