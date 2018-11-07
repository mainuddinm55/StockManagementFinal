package com.kcirque.stockmanagementfinal.Activity;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kcirque.stockmanagementfinal.Adapter.ProductPrintAdapter;
import com.kcirque.stockmanagementfinal.BTPrint.DeviceList;
import com.kcirque.stockmanagementfinal.BTPrint.PrinterCommands;
import com.kcirque.stockmanagementfinal.BTPrint.Utils;
import com.kcirque.stockmanagementfinal.Common.Constant;
import com.kcirque.stockmanagementfinal.Database.Model.Sales;
import com.kcirque.stockmanagementfinal.Fragment.SalesFragment;
import com.kcirque.stockmanagementfinal.R;
import com.kcirque.stockmanagementfinal.databinding.ActivityPrintPreviewBinding;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class PrintPreviewActivity extends AppCompatActivity {
    private ActivityPrintPreviewBinding mBinding;
    byte FONT_TYPE;
    private static BluetoothSocket btsocket;
    private static OutputStream outputStream;
    private Sales sales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_print_preview);
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("PRINT PREVIEW");
        final Intent intent = getIntent();
        if (intent != null) {
            sales = (Sales) intent.getSerializableExtra(Constant.EXTRA_SALES);
            if (sales != null) {
                mBinding.nameTextView.setText("Customer Name: " + sales.getCustomerName());
                mBinding.dateTextView.setText("Date: " + getDateTime()[0]);
                mBinding.productListRecyclerView.setHasFixedSize(true);
                mBinding.productListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                ProductPrintAdapter adapter = new ProductPrintAdapter();
                mBinding.productListRecyclerView.setAdapter(adapter);
                adapter.setProductSellList(sales.getSelectedProduct());
                mBinding.subTotalAmountTextView.setText(String.valueOf(sales.getSubtotal()));
                mBinding.discountAmountTextView.setText(String.valueOf(sales.getDiscount()));
                mBinding.totalAmountTextView.setText(String.valueOf(sales.getTotal()));
                mBinding.paidAmountTextView.setText(String.valueOf(sales.getPaid()));
                mBinding.dueAmountTextView.setText(String.valueOf(sales.getDue()));
                mBinding.printBill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isPreview = intent.getBooleanExtra(Constant.EXTRA_PRINT_PREVIEW, false);
                        if (!isPreview) {
                            printBill(sales);
                        } else {
                            Toast.makeText(PrintPreviewActivity.this, "its Only Preview", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mBinding.skip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(SalesFragment.PRINT_RESULT_CODE);
                        finish();
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void printBill(Sales sales) {
        if (btsocket == null) {
            Intent BTIntent = new Intent(getApplicationContext(), DeviceList.class);
            this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
        } else {
            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = opstream;

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                outputStream = btsocket.getOutputStream();
                byte[] printformat = new byte[]{0x1B, 0x21, 0x03};
                outputStream.write(printformat);


                printCustom("K Cirque IT", 2, 1);
                //printPhoto(R.drawable.ic_icon_pos);
                printCustom("H-1/C, R-02, Sector-3, Uttara, Dhaka-1230", 0, 1);
                printCustom("Hot Line: +88777 888661", 0, 1);
                printCustom("Customer Name: " + sales.getCustomerName(), 0, 1);
                String dateTime[] = getDateTime();
                printText(leftRightAlign(dateTime[0], dateTime[1]));
                printText(leftRightAlign("Qty: Name", "Price "));
                printCustom(new String(new char[32]).replace("\0", "."), 0, 1);
                printText(leftRightAlign("Sub Total", sales.getSubtotal() + "/= "));
                printText(leftRightAlign("Discount", sales.getDiscount() + "/= "));
                printText(leftRightAlign("Total", sales.getTotal() + "/= "));
                printText(leftRightAlign("Paid", sales.getPaid() + "/= "));
                printText(leftRightAlign("Due", sales.getDue() + "/= "));
                printNewLine();
                printCustom("Thank you for coming & we look", 0, 1);
                printCustom("forward to serve you again", 0, 1);
                printNewLine();
                printNewLine();

                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void printDemo() {
        if (btsocket == null) {
            Intent BTIntent = new Intent(getApplicationContext(), DeviceList.class);
            this.startActivityForResult(BTIntent, DeviceList.REQUEST_CONNECT_BT);
        } else {
            OutputStream opstream = null;
            try {
                opstream = btsocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = opstream;

            //print command
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                outputStream = btsocket.getOutputStream();

                byte[] printformat = {0x1B, 0 * 21, FONT_TYPE};
                //outputStream.write(printformat);

                //print title
                printUnicode();
                //print normal text
                //printCustom(message.getText().toString(), 0, 0);
                //printPhoto(R.drawable.img);
                printNewLine();
                printText("     >>>>   Thank you  <<<<     "); // total 32 char in a single line
                //resetPrint(); //reset printer
                printUnicode();
                printNewLine();
                printNewLine();

                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //print custom
    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B, 0x21, 0x03};  // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B, 0x21, 0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text
        try {
            switch (size) {
                case 0:
                    outputStream.write(cc);
                    break;
                case 1:
                    outputStream.write(bb);
                    break;
                case 2:
                    outputStream.write(bb2);
                    break;
                case 3:
                    outputStream.write(bb3);
                    break;
            }

            switch (align) {
                case 0:
                    //left align
                    outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
                    break;
                case 1:
                    //center align
                    outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                    break;
                case 2:
                    //right align
                    outputStream.write(PrinterCommands.ESC_ALIGN_RIGHT);
                    break;
            }
            outputStream.write(msg.getBytes());
            outputStream.write(PrinterCommands.LF);
            //outputStream.write(cc);
            //printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print photo
    public void printPhoto(int img) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                    img);
            if (bmp != null) {
                byte[] command = Utils.decodeBitmap(bmp);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                printText(command);
            } else {
                Log.e("Print Photo error", "the file isn't exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PrintTools", "the file isn't exists");
        }
    }

    //print unicode
    public void printUnicode() {
        try {
            outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
            printText(Utils.UNICODE_TEXT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //print new line
    private void printNewLine() {
        try {
            outputStream.write(PrinterCommands.FEED_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void resetPrint() {
        try {
            outputStream.write(PrinterCommands.ESC_FONT_COLOR_DEFAULT);
            outputStream.write(PrinterCommands.FS_FONT_ALIGN);
            outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
            outputStream.write(PrinterCommands.ESC_CANCEL_BOLD);
            outputStream.write(PrinterCommands.LF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //print text
    private void printText(String msg) {
        try {
            // Print normal text
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //print byte[]
    private void printText(byte[] msg) {
        try {
            // Print normal text
            outputStream.write(msg);
            printNewLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String leftRightAlign(String str1, String str2) {
        String ans = str1 + str2;
        if (ans.length() < 31) {
            int n = (31 - str1.length() + str2.length());
            ans = str1 + new String(new char[n]).replace("\0", " ") + str2;
        }
        return ans;
    }


    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime[] = new String[2];
        dateTime[0] = c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR);
        dateTime[1] = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
        return dateTime;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (btsocket != null) {
                outputStream.close();
                btsocket.close();
                btsocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            btsocket = DeviceList.getSocket();
            if (btsocket != null) {
                if (sales != null) {
                    printBill(sales);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
