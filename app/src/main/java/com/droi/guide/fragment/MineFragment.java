package com.droi.guide.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.droi.guide.R;
import com.droi.guide.activity.AboutUsActivity;
import com.droi.guide.activity.LoginActivity;
import com.droi.guide.activity.MyAnswerActivity;
import com.droi.guide.activity.MyFavoriteActivity;
import com.droi.guide.activity.MyFollowActivity;
import com.droi.guide.activity.ProfileActivity;
import com.droi.guide.activity.QuestionListActivity;
import com.droi.guide.activity.WriteAnswerActivity;
import com.droi.guide.model.Article;
import com.droi.guide.model.Banner;
import com.droi.guide.model.GuideUser;
import com.droi.guide.views.CircleImageView;
import com.droi.sdk.DroiCallback;
import com.droi.sdk.DroiError;
import com.droi.sdk.analytics.DroiAnalytics;
import com.droi.sdk.core.DroiFile;
import com.droi.sdk.core.DroiUser;
import com.droi.sdk.feedback.DroiFeedback;
import com.droi.sdk.selfupdate.DroiUpdate;
import com.droi.sdk.push.DroiPush;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chenpei on 2016/5/12.
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    private static String TAG = "MineFragment";
    private Context mContext;
    @BindView(R.id.head_icon)
    CircleImageView titleImg;
    @BindView(R.id.user_name)
    TextView nameTextView;
    @BindView(R.id.push_switch)
    Switch pushSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, view);
        initUI(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
        DroiAnalytics.onFragmentStart(getActivity(), "MineFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        DroiAnalytics.onFragmentEnd(getActivity(), "MineFragment");
    }

    /**
     * 当登录成功或者登出时刷新View
     */
    private void refreshView() {
        GuideUser user = DroiUser.getCurrentUser(GuideUser.class);
        if (user != null && user.isAuthorized() && !user.isAnonymous()) {
            nameTextView.setText(user.getUserId());
            if (user.avatar != null) {
                user.avatar.getInBackground(new DroiCallback<byte[]>() {
                    @Override
                    public void result(byte[] bytes, DroiError error) {
                        if (error.isOk()) {
                            if (bytes == null) {
                                Log.i(TAG, "bytes == null");
                            } else {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                titleImg.setImageBitmap(bitmap);
                            }
                        }
                    }
                }, null);
            }
        } else {
            titleImg.setImageResource(R.drawable.profile_default_icon);
            nameTextView.setText(R.string.fragment_mine_login);
        }
    }

    private void initUI(View view) {
        view.findViewById(R.id.mine_frag_follow).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_favorite).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_answer).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_question).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_update).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_feedback).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_upload).setOnClickListener(this);
        view.findViewById(R.id.mine_frag_push).setOnClickListener(this);
        view.findViewById(R.id.mine_about_us).setOnClickListener(this);
        view.findViewById(R.id.head_icon).setOnClickListener(this);
        view.findViewById(R.id.right_layout).setOnClickListener(this);
        pushSwitch.setChecked(DroiPush.getPushEnabled(mContext));
        pushSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DroiPush.setPushEnabled(mContext, isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {
        GuideUser user = DroiUser.getCurrentUser(GuideUser.class);
        switch (v.getId()) {
            case R.id.head_icon:

            case R.id.right_layout:
                if (user != null && user.isAuthorized() && !user.isAnonymous()) {
                    toProfile();
                } else {
                    toLogin();
                }
                break;
            case R.id.mine_frag_follow:
                Log.i("test", "follow");
                Intent followIntent = new Intent(getActivity(), MyFollowActivity.class);
                startActivity(followIntent);
                break;
            case R.id.mine_frag_favorite:
                Log.i("test", "favorite");
                Intent favoriteIntent = new Intent(getActivity(), MyFavoriteActivity.class);
                startActivity(favoriteIntent);
                break;
            case R.id.mine_frag_answer:
                Log.i("test", "answer");
                Intent answerIntent = new Intent(getActivity(), MyAnswerActivity.class);
                startActivity(answerIntent);
                break;
            case R.id.mine_frag_question:
                Log.i("test", "question");
                if (user == null) {
                    break;
                }
                Intent questionIntent = new Intent(getActivity(), QuestionListActivity.class);
                questionIntent.putExtra(QuestionFragment.QUESTIONER, user.getObjectId());
                startActivity(questionIntent);
                break;
            case R.id.mine_frag_update:
                //手动更新
                DroiUpdate.manualUpdate(mContext);
                break;
            case R.id.mine_frag_feedback:
                //自定义部分颜色
                DroiFeedback.setTitleBarColor(getResources().getColor(R.color.top_bar_background));
                DroiFeedback.setSendButtonColor(getResources().getColor(R.color.top_bar_background),
                        getResources().getColor(R.color.top_bar_background));
                //打开反馈页面
                DroiFeedback.callFeedback(mContext);
                break;
            case R.id.mine_about_us:
                Log.i("test", "about");
                Intent aboutUsIntent = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(aboutUsIntent);
                break;
            case R.id.mine_frag_upload:
                Log.i("TEST", "mine_frag_upload");
                uploadBanner();
                //uploadAppInfo();
                //uploadAppType();
                uploadOfficialGuide();
                break;
            default:
                break;
        }
    }

    /**
     * 转到登录页面
     */
    private void toLogin() {
        Intent loginIntent = new Intent(mContext, LoginActivity.class);
        startActivity(loginIntent);
    }

    /**
     * 转到个人信息页面
     */
    private void toProfile() {
        Intent profileIntent = new Intent(mContext, ProfileActivity.class);
        startActivity(profileIntent);
    }

    private void uploadBanner() {
        Banner banner = new Banner();
        File imageFile = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "1.png");
        banner.img = new DroiFile(imageFile);
        banner.saveInBackground(null);

        Banner banner2 = new Banner();
        File imageFile2 = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "2.png");
        banner2.img = new DroiFile(imageFile2);
        banner2.saveInBackground(null);
    }

    private void uploadOfficialGuide() {
        //1
        Article officialGuide1 = new Article();
        officialGuide1.type = 2;
        officialGuide1.title = "上海居住证办理";
        officialGuide1.brief = "上海居住证怎么办理?上海居住证办理条件是什么?上海居住证办理需要什么条件?上海居住证在哪里办理?";
        officialGuide1.category = "credential";
        officialGuide1.location = "上海";
        officialGuide1.body = "\n" +
                "<h1>办理条件</h1>\n" +
                "<p>符合下列条件的来沪人员，可以按规定申请办理《上海市居住证》：<br/>\n" +
                "(1)在本市合法稳定居住;<br/>\n" +
                "(2)在本市合法稳定就业，参加本市职工社会保险满6个月(具体条件为申请当月处于就业及缴纳本市职工社会保险状态，且申请当月前12个月内累计缴纳本市职工社会保险费满6个月(不含补缴));\n" +
                "或者因投靠具有本市户籍亲属、就读、进修等需要在本市居住6个月以上。\n" +
                "</p>\n" +
                "<h1>办理材料</h1>\n" +
                "<p>\n" +
                "申请表<br/>\n" +
                "《上海市居住证》申请表;<br/>\n" +
                "身份证<br/>\n" +
                "居民身份证等有效身份证明;<br/>\n" +
                "住所证明<br/>\n" +
                "拟在本市居住6个月以上的住所证明。<br/>\n" +
                "①居住在自购住房的，须提供相应的房地产权证复印件(验原件);<br/>\n" +
                "②居住在租赁住房内的，须提供由房管部门出具的房屋租赁合同登记备案证明复印件(验原件);<br/>\n" +
                "③居住在单位集体宿舍的，须提供单位出具的集体宿舍证明;<br/>\n" +
                "④居住在亲戚朋友家的，须提供居(村)委出具的寄宿证明。<br/>\n" +
                "其他材料<br/>\n" +
                "除上述规定的基本材料外，申请人还应当根据自身情况和需要提供相应补充证明材料。<br/>\n" +
                "(1)来沪就业的，提供期限为6个月以上劳动(聘用)合同复印件(验原件)，申请当月处于就业及缴纳本市职工社会保险状态，且申请当月前12个月内累计缴纳本市职工社会保险费满6个月(不含补缴)的证明;<br/>\n" +
                "(2)投资开业或从事个体经营的，提供企业或个体工商户营业执照复印件(验原件)，申请当月处于就业及缴纳本市职工社会保险状态，且申请当月前12个月内累计缴纳本市职工社会保险费满6个月(不含补缴)的证明。<br/>\n" +
                "(3)来沪投靠本市户籍亲属的配偶、子女或父母的，应分别提供结婚证复印件(验原件)、公安部门认定的父母子女关系的证明;<br/>\n" +
                "(4)来沪就读、进修的，应分别提供本市大、中专院校发出的“录取通知书”复印件(验原件)、本市非学历教育机构出具的书面证明复印件(验原件)。<br/>\n" +
                "</p>\n" +
                "<h1>办理流程</h1>\n" +
                "1、本人备齐证明材料到居住地社区事务受理服务中心提出申请;<br/>\n" +
                "2、填写“《上海市居住证》申请表”，确认后签名;<br/>\n" +
                "3、当场拍摄一寸免冠数码照片(免费);<br/>\n" +
                "4、社区事务受理服务中心收到申办材料后，对材料齐全的，出具“《上海市居住证》受理回执”;对材料不齐全的，当场告知申请人补齐材料。<br/>\n" +
                "5、经主管部门核定后，符合办理条件的，制证;不符合办理条件的，出具书面意见并说明理由，由社区事务受理服务中心告知申请人。<br/>\n" +
                "6、证件制作完成后，社区事务受理服务中心通知申请人领证。<br/>\n" +
                "7、申请人收到通知后到社区事务受理服务中心领证。<br/>\n" +
                "<h1>办理地点</h1>\n" +
                "<p>\n" +
                "申请人到现居住地的社区事务受理服务中心办理。<br/>\n" +
                "上海居住证受理点大全<br/>\n" +
                "办理时限及费用<br/>\n" +
                "(1)社区事务受理服务中心应当于出具“《上海市居住证》受理回执”之日起2个工作日内，将申办信息、申办材料提交区(县)人力资源社会保障局或者公安派出所。<br/>\n" +
                "(2)区(县)人力资源社会保障局、公安派出所收到申办信息、申办材料后，应在10个工作日内完成核定。<br/>\n" +
                "(3)公安部门应当自收到制证通知后10个工作日内完成证件制作。<br/>\n" +
                "(4)社区事务受理服务中心收到《上海市居住证》后，应当在2个工作日内通知申请人前来领证。<br/>\n" +
                "费用：工本费20元\n" +
                "</p>";
        officialGuide1.saveInBackground(null);

        Article officialGuide2 = new Article();
        officialGuide2.type = 2;
        officialGuide2.title = "上海最新落户政策";
        officialGuide2.brief = "上海最新落户政策,2016年上海户口申请条件。";
        officialGuide2.category = "social";
        officialGuide2.location = "上海";
        officialGuide2.body = "<h1>上海建立积分落户政策</h1>\n" +
                "<p>2016年上海户口申请条件。上海常住人口规模要保证在2020年不超过2500万。同时上海就是在作为人口管理措施的时候，上海也是明确的提出要逐步建立积分落户政策。\n" +
                "4月25日，上海市政府官网发布《上海市人民政府关于进一步推进本市户籍制度改革的若干意见》(下称《若干意见》)，并表示，要完善居住证、居住证转办常住户口、直接落户政策，在此基础上，逐步建立积分落户政策。具体来说，就是要根据综合承载能力和经济社会发展需要，以具有合法稳定就业和合法稳定住所、参加城镇社会保险年限、连续居住年限等为主要指标，合理设置积分分值。按照总量控制、公开透明、有序办理、公平公正的原则，对达到规定标准条件的人员，可以申请上海市常住户口。</p>\n" +
                "<h1>从条件管理到积分落户</h1>\n" +
                "<p>积分落户的一个基础是居住证。从2013年7月1日起，上海取消了人才居住证和一般居住证也就是A类和C类居住证的差别，统一施行积分制，并根据不同的积分，为持证者提供梯度化 的公共服务。达到标准分值120分的外来人员，就可以享受包括同住子女参加高考等市民化待遇。而这种市民化待遇，区别于户籍人口之处，在于父母不能投靠、 不能享受上海市低保待遇、不能申请购买共有产权房三项。这个积分体系，包括了基础指标、加分指标、减分指标和一票否决指标。总的来看，持证人越年轻、学历或职称越高、专业越紧缺、纳税或其他社会贡献越多，积分就越高。</p>\n" +
                "<h1>上海户籍新政：建立积分落户制</h1>\n" +
                "<h2>严控2500万人口规模</h2>\n" +
                "<p>《若干意见》的一个重要目标，就是到2020年，上海全市常住人口规模控制在2500万以内，人口结构更加合理，人口素质进一步提升，人口布局进一步优化。本世纪以来，上海常住人口激增800多万，从2000年1608万人，增加到2014年底的2425.68万人。这其中，户籍常住人口1429.26万人，外来常住人口996.42万人。上海第六次全国人口普查资料显示，2010年上海常住人口共2301.92万，也就是说，4年中上海人口增加了123.76万人。而要守住2500万人的底线，就意味着未来几年内，上海的常住人口增加，不能超过74.32万人。</p>\n" +
                "<h2>取消农业户口与非农业户口性质区分</h2>\n" +
                "<p>此外，上海还将建立统一的城乡户口登记制度。取消本市农业户口与非农业户口性质区分，统一登记为居民户口。调整并逐步完善与统一城乡户口登记制度相适应的教育、卫生计生、就业、社会保障、住房、土地及人口统计制度。研究完善上海市居住证管理办法及其配套政策，进一步健全以居住证为载体的基本公共服务及便利提供机制。加强户籍规范管理。健全实有人口登记制度。加强实有人口信息化建设。</p>\n" +
                "<h1>条件</h1>\n" +
                "五类人满足相关要求可直接拿到上海户口<br/>\n" +
                "第一类：创业人才<br/>\n" +
                "直接落户条件：获科技企业孵化器或创业投资机构首轮创业投资额大于1000万元(含1000万元)或累计获得创业投资额大于2000万元(含2000万元)，且在上海本市企业中持股比例不低于10%并连续工作满2年。<br/>\n" +
                "第二类：创新创业中介服务人才<br/>\n" +
                "直接落户条件：在本市技术转移服务机构中连续从事技术转移和科技成果转化服务满2年，且最近3年累计实现技术交易额大于5000万元(含5000万元)的技术合同第一完成人。<br/>\n" +
                "第三类：风险投资管理运营人才<br/>\n" +
                "直接落户条件：本市创业投资机构的合伙人或副总裁及以上的高级管理人才且已完成在上海投资累计达3000万元。<br/>\n" +
                "第四类：企业高级管理和科技技能人才<br/>\n" +
                "直接落户条件：最近4年累计36个月在本市缴纳职工社保的基数等于本市上年度职工社会平均工资3倍，且缴纳个人所得税累计达到100万元。<br/>\n" +
                "第五类：企业家<br/>\n" +
                "<h1>直接落户条件：须同时符合</h1>\n" +
                "1、运营本市企业的法定代表人(担任董事长或总经理)或持股大于10%(含10%)的创始人。<br/>\n" +
                "2、企业连续3年每年营业收入利润率大于10%，且上年度应纳税额大于1000万元(含1000万元)、或科技企业连续3年每年主营业务收入增长大于10%，且上年度应纳税额大于1000万元(含1000万元)或企业在上海证券交易所、深圳证券交易所等资本市场挂牌上市。<br/>\n" +
                "3、企业的生产工艺、装备和产品不属于国家和本市规定的限制类、淘汰类目录。<br/>\n" +
                "4、企业无重大违法违规行为和处罚记录，无不良诚信记录。<br/>";
        officialGuide2.saveInBackground(null);

        Article officialGuide3 = new Article();
        officialGuide3.type = 2;
        officialGuide3.title = "上海婚内新生儿入户需要那些材料？";
        officialGuide3.brief = "上海婚内新生儿入户需要哪些材料？上海婚内新生儿入户需要注意什么？";
        officialGuide3.category = "credential";
        officialGuide3.location = "上海";
        officialGuide3.body = "<h1>国内出生婴儿办理出生登记</h1>\n" +
                "<h2>证明材料</h2>\n" +
                "1、《申报户口事项申请表》、《出生医学证明》,对《出生医学证明》有弄虚作假嫌疑或与事实不符的及新生儿出生在外省市，还须提交母亲产前医院检查、分娩记录、出院小结等凭证，无法提供上述材料的，须提供亲子鉴定证明<br/>\n" +
                "2、父母双方的《结婚证》<br/>\n" +
                "3、父母双方的《居民户口簿》或《户籍证明》、《居民身份证》,父或母系现役军人的，须提供军人身份证件<br/>\n" +
                "4、其他特殊情况须提供的材料<br/>\n" +
                "\n" +
                "<h2>非婚生婴儿办理出生登记的证明材料</h2>\n" +
                "1、父或母的《婚姻状况声明书》；<br/>\n" +
                "2、父或母已婚且随已婚一方报出生的，提交配偶同意接受证明及配偶《居民身份证》；<br/>\n" +
                "3、随母报出生的，提交母亲产前医院检查、分娩记录、出院小结等凭证或亲子鉴定证明；<br/>\n" +
                "4、随父报出生的，提交亲子鉴定证明；<br/>\n" +
                "\n" +
                "<h2>父母均为高校学生集体户口婴儿随（外）祖父母办理出生登记的证明材料</h2>\n" +
                "1、（外）祖父母《居民户口簿》、《居民身份证》<br/>\n" +
                "2、父亲与祖父母或母亲与外祖父母的亲属关系证明（《户籍证明》、人事档案等）<br/>\n" +
                "\n" +
                "<h2>父或母为旅居国（境）外的原本市居民，其在国内出生、具有中国国籍的婴儿随（外）祖父母办理出生登记的证明材料</h2>\n" +
                "1、（外）祖父母《居民户口簿》、《居民身份证》<br/>\n" +
                "2、父亲与祖父母或母亲与外祖父母的亲属关系证明（《户籍证明》、人事档案等）<br/>\n" +
                "3、父或母《中华人民共和国护照》、原户口注销的《户籍证明》<br/>\n" +
                "\n" +
                "<h2>父或母系现役军人的婴儿办理出生登记的证明材料</h2>\n" +
                "1、母亲系驻沪部队军人，婴儿随母入户部队集体户的，须提供部队师（旅）级政治部门证明以及集体户管理部门的同意入户意见书<br/>\n" +
                "2、父或母为本市居民应征入伍，婴儿随（外）祖父母办理出生登记的，须提供：<br/>\n" +
                "（1）（外）祖父母《居民户口簿》、《居民身份证》<br/>\n" +
                "（2）父亲与祖父母或母亲与外祖父母的亲属关系证明（《户籍证明》、人事档案等）<br/>\n" +
                "（3）父或母在本市注销户口的《户籍证明》及部队师（旅）级政治部门证明<br/>\n" +
                "说明：<br/>\n" +
                "（一）申请人应当提供证明材料原件及复印件（证明材料要求提供原件的除外），并由申请人、受理民警双方在复印件上签名确认该复印件与原件的一致性。对不能提供原件的，应由存档单位在复印件上加盖公章确认；复印件有多页的，应加盖骑缝章。<br/>\n" +
                "（二）在本市办理出生登记，统一登记为非农业户口。<br/>\n" +
                "（三）驻沪部队的男军人或者非驻沪部队的女军人所生婴儿不能在本市部队集体户口所在地申报出生登记。<br/>\n" +
                "（四）超过一年未办理出生登记的婴儿申报出生的，提供的《申报户口事项申请表》应由其父母填写并由父母双方联合签名。<br/>\n" +
                "（五）提交的证明材料应真实、有效。一经查实弄虚作假非法报入户口或与事实不符的，户口将予以注销，并依法追究相关人员的法律责任。<br/>\n" +
                "（六）申请人按服务告知单提供材料，公安机关经审核后，认为所提供的材料前后矛盾或未能真实反映情况，确有必要出具其他证明，申请人应按要求提供。<br/>\n";
        officialGuide3.saveInBackground(null);

        Article officialGuide4 = new Article();
        officialGuide4.type = 2;
        officialGuide4.title = "上海违章处理";
        officialGuide4.brief = "上海违章怎么处理,到哪里处理?";
        officialGuide4.category = "transport";
        officialGuide4.location = "上海";
        officialGuide4.body = "<h1>网上处理</h1>\n" +
                "上海交通违章网上处理网址:https://sh.122.gov.cn/m/showBussi.html\n" +
                "<h2>可办理事项：</h2>\n" +
                "用户可以给绑定的机动车、驾驶证的未缴款违法记录进行缴款。<br/>\n" +
                "网上处理流程：<br/>\n" +
                "1，登录网上缴费平台http://sh.122.gov.cn/<br/>\n" +
                "2，点击下方的违法处理业务--缴纳罚款-在线办理，进入办理页面进行操作。<br/>\n" +
                "3，没有注册的用户需要先注册在办理，已经注册的用户输入登录信息进入后台操作即可。<br/>\n" +
                "\n" +
                "<h1>网点办理</h1>\n" +
                "办理流程：<br/>\n" +
                "第一步：违法处理。<br/>\n" +
                "1、被路面执勤民警当场开具的简易处罚决定书，交通违法行为人应持其第三联(交银行联)于开具决定书之日起十五日内，前往银行缴纳罚款，逾期不缴纳罚款的，每日按罚款数额的3%加处罚款，加处罚款总额不得超出罚款数额。<br/>\n" +
                "2、因交通违法行为被路面执勤民警依法扣留机动车的，交通违法行为人应持车辆扣留凭证、本人驾驶证(驾驶证被依法扣留的和无驾驶证的持本人身份证)和机动车行驶证(或发票和合格证)15日内到交警大队处罚教育室窗口接受处理。<br/>\n" +
                "3、机动车因交通违法行为被电子监控设备记录违法信息的，交通违法行为人或者车辆所有人、管理人可在获知违法信息后持本人驾驶证和车辆行驶证，前往交警大队交通违法处理窗口接受处理。<br/>\n" +
                "上海市公安局交警总队车辆管理所<br/>\n" +
                "业务咨询请拨：12345<br/>\n" +
                "窗口接待地点：<br/>\n" +
                "车辆管理所一分所：长宁区哈密路1330号<br/>\n" +
                "(长宁、普陀、静安、闸北、宝山和嘉定)<br/>\n" +
                "车辆管理所二分所：闵行区莘庄沁春路179号<br/>\n" +
                "(闵行、徐汇、松江、金山和青浦)<br/>\n" +
                "车辆管理所三分所：浦东新区沪南公路2638号<br/>\n" +
                "(浦东新区、黄浦、虹口、杨浦、奉贤和崇明)<br/>\n" +
                "电子违章<br/>\n" +
                "电子监控处理网址：https://sh.122.gov.cn/m/login?(注册登陆后即可办理)<br/>\n" +
                "1、 用户处理电子监控记录前要需要绑定本人的机动车及驾驶证。<br/>\n" +
                "2、 用户只能处理罚款金额为200元以下的电子监控记录。<br/>\n" +
                "3、 用户处理电子监空记录后驾驶证累记记分达12分的不允许处理。<br/>\n" +
                "4、 缴款罚款在线支付成功后电子监控才算处理成功。<br/>\n" +
                "详细办理流程<br/>\n" +
                "罚款缴纳<br/>\n" +
                "1、外省市号牌机动车在本市的违法记录已转递至机动车登记地的，也可以到机动车登记地公安机关交通管理部门接受处理;<br/>\n" +
                "2、本市号牌机动车在外省市的违法行为，也可以到违法行为发生地公安机关交通管理部门接受处理;<br/>\n" +
                "3、对已在本市接受处罚但尚未缴纳罚款的，可通过本市工商银缴纳罚款;<br/>\n" +
                "4、对已在外省市接受处罚但尚未缴纳罚款的，请至处罚机关所在地缴纳罚款。<br/>\n" +
                "5、对违法事实无异议且罚款金额为200元以下(含)的，违法车辆驾驶人可持本人工商银行上海分行牡丹畅通卡通过<br/>\n" +
                "(1)上海市工商银行的电话银行(95588)<br/>\n" +
                "(2)网上银行(www.icbc.com.cn或www.sh.icbc.com.cn)<br/>\n" +
                "(3)多媒体自助终端自助处理并缴纳罚款(同时实行记分管理)。<br/>\n" +
                "6、对违法事实无异议，但违法车辆驾驶人没有办理牡丹畅通卡或者使用牡丹畅通卡不予自助处理的，可到本市各交警支(大)队违法受理点接受处理。<br/>";
        officialGuide4.saveInBackground(null);

        Article officialGuide5 = new Article();
        officialGuide5.type = 2;
        officialGuide5.title = "上海婚育证明办理办理";
        officialGuide5.brief = "什么是婚育证明？婚育证明是指计生部门开具的证明当事人婚育状态的证明文件。";
        officialGuide5.category = "wedding";
        officialGuide5.location = "上海";
        officialGuide5.body = "<h1>1、办理机构</h1>\n" +
                "<p>本市户籍人员，应当在户籍所在地的镇（乡）人民政府、街道办事处办理《婚育证明》，来沪人员，未在户籍地办理《婚育证明》或者《婚育证明》已过有效期，符合规定条件的，可以在本市现居住地镇（乡）人民政府、街道办事处申请办理《婚育证明》；不符合在本市办理《婚育证明》条件的来沪人员，应当在规定的期限内补办户籍地《婚育证明》，并在限期补办期间在本市现居住地镇（乡）人民政府、街道办事处办理临时性《婚育证明》。</p>\n" +
                "<h1>2、办理材料</h1>\n" +
                "本市户籍人员办理《婚育证明》，需提供以下材料：<br/>\n" +
                "1）身份证或者身份证明；<br/>\n" +
                "2）本人近期一张免冠照片；<br/>\n" +
                "3）婚姻状况证明；<br/>\n" +
                "4）户口簿；<br/>\n" +
                "5）已有子女状况声明。<br/>\n" +
                "来沪人员办理《婚育证明》，需提供以下材料：<br/>\n" +
                "1）身份证或者身份证明；<br/>\n" +
                "2）本人近期一张免冠照片；<br/>\n" +
                "3）户籍地镇（乡）级以上人口计生部门出具的婚姻生育情况证明；<br/>\n" +
                "4）在本市有住所证明（租赁房屋或者拥有产权房等证明）或者在本市有稳定职业证明（工商登记、劳动合同、单位出具的雇佣关系证明等）。<br/>\n" +
                "办理临时《婚育证明》的来沪人员，需提供：<br/>\n" +
                "1）身份证或者身份证明；<br/>\n" +
                "2）本人近期一张免冠照片；<br/>\n" +
                "3）婚姻生育情况声明。<br/>\n" +
                "<h1>3、办理时限：</h1>\n" +
                "<p>对于材料齐全、办理《婚育证明》的申请，应当当场或者自受理之日起5个工作日内，给予办理；</p>\n" +
                "<p>对于材料齐全、办理临时性《婚育证明》的申请，应当当场或者自受理之日起2个工作日内，给予办理。<p>";
        officialGuide5.saveInBackground(null);

 /*       Article officialGuide6 = new Article();
        officialGuide6.type = 2;
        officialGuide6.title = "上海居住证办理";
        officialGuide6.brief = "上海居住证怎么办理?上海居住证办理条件是什么?上海居住证办理需要什么条件?上海居住证在哪里办理?";
        officialGuide6.category = "credential";
        officialGuide6.location = "上海";
        officialGuide6.body = "";
        officialGuide6.saveInBackground(null);

        Article officialGuide7 = new Article();
        officialGuide7.type = 2;
        officialGuide7.title = "上海居住证办理";
        officialGuide7.brief = "上海居住证怎么办理?上海居住证办理条件是什么?上海居住证办理需要什么条件?上海居住证在哪里办理?";
        officialGuide7.category = "credential";
        officialGuide7.location = "上海";
        officialGuide7.body = "";
        officialGuide7.saveInBackground(null);

        Article officialGuide8 = new Article();
        officialGuide8.type = 2;
        officialGuide8.title = "上海居住证办理";
        officialGuide8.brief = "上海居住证怎么办理?上海居住证办理条件是什么?上海居住证办理需要什么条件?上海居住证在哪里办理?";
        officialGuide8.category = "credential";
        officialGuide8.location = "上海";
        officialGuide8.body = "";
        officialGuide8.saveInBackground(null);*/
    }
}
