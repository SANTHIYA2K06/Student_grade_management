import React, { useState, useEffect } from 'react';
import { Layout, Table, Button, message, Card, Row, Col, Statistic } from 'antd';
import { ReloadOutlined } from '@ant-design/icons';
import api from '../api';

const { Content } = Layout;

const StaffModuleList = ({ staffId }) => {
  const [modules, setModules] = useState([]);
  const [loading, setLoading] = useState(false);
  const [moduleStats, setModuleStats] = useState([]);

  const fetchModules = async () => {
    setLoading(true);
    try {
      const response = await api.get('/api/module/list', {
        params: {
          current: 1,
          size: 100,
          staffId: staffId
        }
      });

      if (response.data?.data?.records) {
        const moduleRecords = response.data.data.records;
        setModules(moduleRecords);

        // Fetch statistics for each module
        const statsPromises = moduleRecords.map(module => 
          api.get('/api/record', {
            params: {
              moduleCode: module.code,
              current: 1,
              size: 100
            }
          })
        );

        const statsResponses = await Promise.all(statsPromises);
        const statistics = statsResponses.map((response, index) => {
          const records = response.data?.data?.records || [];
          return {
            moduleCode: moduleRecords[index].code,
            averageScore: records.reduce((sum, r) => sum + (r.averageScore || 0), 0) / (records.length || 1),
            passRate: records.reduce((sum, r) => sum + (r.passRate || 0), 0) / (records.length || 1),
            studentCount: records.reduce((sum, r) => sum + (r.numberOfCandidates || 0), 0)
          };
        });
        setModuleStats(statistics);
      }
    } catch (error) {
      message.error('Failed to fetch modules');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchModules();
  }, [staffId]);

  const calculateOverallStats = () => {
    if (!moduleStats.length) return null;

    return {
      totalModules: moduleStats.length,
      totalStudents: moduleStats.reduce((sum, stat) => sum + stat.studentCount, 0),
      averageScore: (moduleStats.reduce((sum, stat) => sum + stat.averageScore, 0) / moduleStats.length).toFixed(1),
      averagePassRate: (moduleStats.reduce((sum, stat) => sum + stat.passRate, 0) / moduleStats.length * 100).toFixed(1)
    };
  };

  const columns = [
    {
      title: 'Module Code',
      dataIndex: 'code',
      key: 'code',
      sorter: (a, b) => a.code.localeCompare(b.code)
    },
    {
      title: 'Module Name',
      dataIndex: 'name',
      key: 'name',
      sorter: (a, b) => a.name.localeCompare(b.name)
    },
    {
      title: 'Credits',
      dataIndex: 'credits',
      key: 'credits',
      sorter: (a, b) => a.credits - b.credits
    },
    {
      title: 'Student Count',
      key: 'studentCount',
      render: (_, record) => {
        const stats = moduleStats.find(s => s.moduleCode === record.code);
        return stats?.studentCount || 0;
      },
      sorter: (a, b) => {
        const statsA = moduleStats.find(s => s.moduleCode === a.code);
        const statsB = moduleStats.find(s => s.moduleCode === b.code);
        return (statsA?.studentCount || 0) - (statsB?.studentCount || 0);
      }
    },
    {
      title: 'Average Score',
      key: 'averageScore',
      render: (_, record) => {
        const stats = moduleStats.find(s => s.moduleCode === record.code);
        return stats?.averageScore ? `${stats.averageScore.toFixed(1)}%` : '-';
      },
      sorter: (a, b) => {
        const statsA = moduleStats.find(s => s.moduleCode === a.code);
        const statsB = moduleStats.find(s => s.moduleCode === b.code);
        return (statsA?.averageScore || 0) - (statsB?.averageScore || 0);
      }
    },
    {
      title: 'Pass Rate',
      key: 'passRate',
      render: (_, record) => {
        const stats = moduleStats.find(s => s.moduleCode === record.code);
        return stats?.passRate ? `${(stats.passRate * 100).toFixed(1)}%` : '-';
      },
      sorter: (a, b) => {
        const statsA = moduleStats.find(s => s.moduleCode === a.code);
        const statsB = moduleStats.find(s => s.moduleCode === b.code);
        return (statsA?.passRate || 0) - (statsB?.passRate || 0);
      }
    }
  ];

  return (
    <Content style={{ margin: '24px 16px 0', padding: 24, background: '#fff' }}>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>My Modules</h2>
        <Button icon={<ReloadOutlined />} onClick={fetchModules} loading={loading} />
      </div>

      {modules.length > 0 && calculateOverallStats() && (
        <Card title="Teaching Overview" style={{ marginBottom: 24 }}>
          <Row gutter={[16, 16]}>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#f0f5ff' }}>
                <Statistic
                  title="Total Modules"
                  value={calculateOverallStats().totalModules}
                  valueStyle={{ color: '#1890ff' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#f6ffed' }}>
                <Statistic
                  title="Total Students"
                  value={calculateOverallStats().totalStudents}
                  valueStyle={{ color: '#52c41a' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#fff0f6' }}>
                <Statistic
                  title="Average Pass Rate"
                  value={calculateOverallStats().averagePassRate}
                  suffix="%"
                  valueStyle={{ color: '#eb2f96' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#f0f2ff' }}>
                <Statistic
                  title="Average Score"
                  value={calculateOverallStats().averageScore}
                  suffix="%"
                  valueStyle={{ color: '#722ed1' }}
                />
              </Card>
            </Col>
          </Row>
        </Card>
      )}

      <Table
        columns={columns}
        dataSource={modules}
        rowKey="code"
        loading={loading}
        onRow={(record) => ({
          onClick: () => window.location.href = `/module/${record.code}`,
          style: { cursor: 'pointer' }
        })}
      />
    </Content>
  );
};

export default StaffModuleList; 